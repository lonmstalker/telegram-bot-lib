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
package io.github.tgkit.security.captcha.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tgkit.api.BotRequest;
import io.github.tgkit.internal.config.BotGlobalConfig;
import io.github.tgkit.api.dsl.Button;
import io.github.tgkit.api.exception.BotApiException;
import io.github.tgkit.security.captcha.CaptchaProvider;
import io.github.tgkit.security.secret.SecretStore;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Google reCAPTCHA v3: тихий fallback-провайдер. */
public final class RecaptchaWebProvider implements CaptchaProvider {

  private static final Logger log = LoggerFactory.getLogger(RecaptchaWebProvider.class);
  private final @NonNull String verifyUrl;
  private final @NonNull String domain;
  private final @NonNull String secretKey;
  private final @NonNull ObjectMapper mapper;
  private final @NonNull HttpClient httpClient;

  private RecaptchaWebProvider(
      @NonNull String domain,
      @Nullable String secretKey,
      @Nullable SecretStore secretStore,
      @Nullable String verifyUrl,
      @Nullable ObjectMapper mapper,
      @Nullable HttpClient httpClient) {
    if (secretStore == null && secretKey == null) {
      throw new IllegalArgumentException("recaptcha secret required");
    }

    this.httpClient = httpClient != null ? httpClient : BotGlobalConfig.INSTANCE.http().getClient();
    this.secretKey =
        secretKey != null
            ? secretKey
            : secretStore
                .get("RECAPTCHA_SECRET_KEY")
                .orElseThrow(() -> new IllegalArgumentException("recaptcha site key required"));

    this.domain = domain;
    this.mapper = mapper != null ? mapper : BotGlobalConfig.INSTANCE.http().getMapper();
    this.verifyUrl =
        verifyUrl != null ? verifyUrl : "https://www.google.com/recaptcha/api/siteverify";
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public @NonNull SendMessage question(@NonNull BotRequest<?> r) {
    String url = String.format("https://%s/recaptcha?uid=%s", domain, r.user().userId());
    return r.msgKey("recaptcha.message")
        .keyboard(kb -> kb.row(Button.webAppKey("recaptcha.button", url)))
        .disableNotif()
        .build();
  }

  /** REST-endpoint вызывается фронтом после Google check. */
  @Override
  @SuppressWarnings("unchecked")
  public boolean verify(@NonNull BotRequest<?> request, @NonNull String answer) {
    String form =
        "secret="
            + URLEncoder.encode(secretKey, StandardCharsets.UTF_8)
            + "&response="
            + URLEncoder.encode(answer, StandardCharsets.UTF_8);

    HttpRequest req =
        HttpRequest.newBuilder()
            .uri(URI.create(verifyUrl))
            .timeout(Duration.ofSeconds(5))
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build();

    try {
      HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

      Map<String, Object> map = mapper.readValue(resp.body(), Map.class);

      boolean ok = Boolean.TRUE.equals(map.get("success"));
      if (!ok) {
        log.warn("reCAPTCHA failed (uid={}): {}", request.user().userId(), resp.body());
      }
      return ok;
    } catch (Exception e) {
      throw new BotApiException(e);
    }
  }

  public static final class Builder {
    private String domain;
    private String secretKey;
    private SecretStore secretStore;
    private String verifyUrl;
    private ObjectMapper mapper;
    private HttpClient httpClient;

    public Builder domain(@NonNull String domain) {
      this.domain = domain;
      return this;
    }

    public Builder secretKey(@Nullable String secretKey) {
      this.secretKey = secretKey;
      return this;
    }

    public Builder secretStore(@Nullable SecretStore secretStore) {
      this.secretStore = secretStore;
      return this;
    }

    public Builder verifyUrl(@Nullable String verifyUrl) {
      this.verifyUrl = verifyUrl;
      return this;
    }

    public Builder mapper(@Nullable ObjectMapper mapper) {
      this.mapper = mapper;
      return this;
    }

    public Builder httpClient(@Nullable HttpClient client) {
      this.httpClient = client;
      return this;
    }

    public RecaptchaWebProvider build() {
      return new RecaptchaWebProvider(
          domain, secretKey, secretStore, verifyUrl, mapper, httpClient);
    }
  }
}
