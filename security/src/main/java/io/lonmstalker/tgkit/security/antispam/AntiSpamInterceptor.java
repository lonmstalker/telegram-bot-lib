package io.lonmstalker.tgkit.security.antispam;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Anti-Spam / Anti-Flood перехватчик.
 *
 * <ul>
 *   <li>Flood-gate — {@link io.lonmstalker.tgkit.security.ratelimit.RateLimiter}</li>
 *   <li>Duplicate-guard — {@link DuplicateProvider}</li>
 *   <li>Malicious links — стоп-лист доменов</li>
 *   <li>При срабатывании триггера выдаёт CAPTCHA вместо команды</li>
 * </ul>
 */
@Slf4j
@Builder
@RequiredArgsConstructor
public final class AntiSpamInterceptor implements BotInterceptor {

    private static final Pattern URL_RE =
            Pattern.compile("(https?://[\\w\\-.]+)", Pattern.CASE_INSENSITIVE);

    private final DuplicateProvider dup;
    private final RateLimiter flood;
    private final CaptchaProvider captcha;
    private final Set<String> badDomains;               // конфиг-файл

    /* === preHandle ======================================================= */
    @Override
    public void preHandle(@NonNull Update upd,
                          @NonNull BotRequest<?> request) {

        Message msg = upd.getMessage();
        if (msg == null || msg.getText() == null) return;    // неинтересно

        String txt = msg.getText();
        Integer msgId = request.msgId();
        long chat = Objects.requireNonNull(request.user().chatId());
        long user = Objects.requireNonNull(request.user().userId());

        /* 1) Flood-gate */
        if (!flood.tryAcquire("chat:" + chat, 20, 10) ||
                !flood.tryAcquire("user:" + user, 8, 10)) {
            request.service().sender().execute(captcha.question(request));
            throw new DropUpdateException("flood");
        }

        /* 2) Дубликаты */
        if (dup.isDuplicate(chat, txt)) {
            request.service().sender().execute(captcha.question(request));
            throw new DropUpdateException("duplicate");
        }

        /* 3) Плохие ссылки */
        if (containsBadUrl(txt)) {
            if (msgId != null) {
                request.service().sender().execute(request.delete(msgId).build());
            }
            request.service().sender().execute(request.msgKey("link.blocked").build());
            throw new DropUpdateException("malicious url");
        }
    }

    @Override
    public void postHandle(@NonNull Update u,
                           @NonNull BotRequest<?> request) {
    }

    @Override
    public void afterCompletion(@NonNull Update u,
                                @Nullable BotRequest<?> req,
                                @Nullable BotResponse r,
                                @Nullable Exception e) {
    }

    private boolean containsBadUrl(String text) {
        Matcher m = URL_RE.matcher(text);
        while (m.find()) {
            String host = host(m.group());
            if (badDomains.contains(host) ||
                    host.endsWith(".ru.com") ||
                    host.endsWith(".xyz")) {
                return true;
            }
        }
        return false;
    }

    private static String host(String url) {
        return Optional.ofNullable(url)
                .map(java.net.URI::create)
                .map(java.net.URI::getHost)
                .orElse("");
    }
}
