/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.security.antispam;

import io.github.tgkit.api.BotRequest;
import io.github.tgkit.internal.BotResponse;
import io.github.tgkit.internal.config.BotGlobalConfig;
import io.github.tgkit.api.interceptor.BotInterceptor;
import io.github.tgkit.security.captcha.CaptchaProvider;
import io.github.tgkit.security.event.SecurityBotEvent;
import io.github.tgkit.security.ratelimit.RateLimiter;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Anti-Spam / Anti-Flood перехватчик.
 *
 * <ul>
 *   <li>Flood-gate — {@link io.github.tgkit.security.ratelimit.RateLimiter}
 *   <li>Duplicate-guard — {@link DuplicateProvider}
 *   <li>Malicious links — стоп-лист доменов
 *   <li>При срабатывании триггера выдаёт CAPTCHA вместо команды
 * </ul>
 */
public final class AntiSpamInterceptor implements BotInterceptor {

  private static final Logger log = LoggerFactory.getLogger(AntiSpamInterceptor.class);

  private static final Pattern URL_RE =
      Pattern.compile("(https?://[\\w\\-.]+)", Pattern.CASE_INSENSITIVE);

  private final DuplicateProvider dup;
  private final RateLimiter flood;
  private final CaptchaProvider captcha;
  private final Set<String> badDomains; // конфиг-файл

  public AntiSpamInterceptor(
      @NonNull DuplicateProvider dup,
      @NonNull RateLimiter flood,
      @NonNull CaptchaProvider captcha,
      @NonNull Set<String> badDomains) {
    this.dup = Objects.requireNonNull(dup);
    this.flood = Objects.requireNonNull(flood);
    this.captcha = Objects.requireNonNull(captcha);
    this.badDomains = Objects.requireNonNull(badDomains);
  }

  public static Builder builder() {
    return new Builder();
  }

  private static String host(String url) {
    return Optional.ofNullable(url).map(java.net.URI::create).map(java.net.URI::getHost).orElse("");
  }

  /* === preHandle ======================================================= */
  @Override
  /** Основная логика спам-фильтра: flood, дубликаты и плохие ссылки. */
  public void preHandle(@NonNull Update upd, @NonNull BotRequest<?> request) {

    Message msg = upd.getMessage();
    if (msg == null || msg.getText() == null) {
      return; // неинтересно
    }

    String txt = msg.getText();
    Integer msgId = request.msgId();
    long botId = request.botInfo().internalId();
    long chat = Objects.requireNonNull(request.user().chatId());
    long user = Objects.requireNonNull(request.user().userId());

    /* 1) Flood-gate */
    if (!flood.tryAcquire(botId + ":chat:" + chat, 20, 10)
        || !flood.tryAcquire(botId + ":user:" + user, 8, 10)) {
      request.service().sender().execute(captcha.question(request));
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new SecurityBotEvent(SecurityBotEvent.Type.FLOOD, Instant.now(), request));
      throw new DropUpdateException("flood");
    }

    /* 2) Дубликаты */
    if (dup.isDuplicate(chat, txt)) {
      request.service().sender().execute(captcha.question(request));
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new SecurityBotEvent(SecurityBotEvent.Type.DUPLICATE, Instant.now(), request));
      throw new DropUpdateException("duplicate");
    }

    /* 3) Плохие ссылки */
    if (containsBadUrl(txt)) {
      if (msgId != null) {
        request.service().sender().execute(request.delete(msgId).build());
      }
      request.service().sender().execute(request.msgKey("link.blocked").build());
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(
              new SecurityBotEvent(SecurityBotEvent.Type.MALICIOUS_URL, Instant.now(), request));
      throw new DropUpdateException("malicious url");
    }
  }

  @Override
  public void postHandle(@NonNull Update u, @NonNull BotRequest<?> request) {}

  @Override
  public void afterCompletion(
      @NonNull Update u,
      @Nullable BotRequest<?> req,
      @Nullable BotResponse r,
      @Nullable Exception e) {}

  private boolean containsBadUrl(String text) {
    Matcher m = URL_RE.matcher(text);
    while (m.find()) {
      String host = host(m.group());
      if (badDomains.contains(host) || host.endsWith(".ru.com") || host.endsWith(".xyz")) {
        return true;
      }
    }
    return false;
  }

  public static final class Builder {
    private DuplicateProvider dup;
    private RateLimiter flood;
    private CaptchaProvider captcha;
    private Set<String> badDomains;

    private Builder() {}

    public Builder dup(@NonNull DuplicateProvider dup) {
      this.dup = dup;
      return this;
    }

    public Builder flood(@NonNull RateLimiter flood) {
      this.flood = flood;
      return this;
    }

    public Builder captcha(@NonNull CaptchaProvider captcha) {
      this.captcha = captcha;
      return this;
    }

    public Builder badDomains(@NonNull Set<String> badDomains) {
      this.badDomains = badDomains;
      return this;
    }

    public AntiSpamInterceptor build() {
      return new AntiSpamInterceptor(dup, flood, captcha, badDomains);
    }
  }
}
