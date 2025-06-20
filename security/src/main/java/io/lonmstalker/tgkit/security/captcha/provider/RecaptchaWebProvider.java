package io.lonmstalker.tgkit.security.captcha.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.dsl.Button;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Google reCAPTCHA v3: тихий fallback-провайдер. */
@Slf4j
public final class RecaptchaWebProvider implements CaptchaProvider {
  private final @NonNull String verifyUrl;
  private final @NonNull String domain;
  private final @NonNull String secretKey;
  private final @NonNull ObjectMapper mapper;
  private final @NonNull HttpClient httpClient;

  @Builder
  public RecaptchaWebProvider(
      @NonNull String domain,
      @Nullable String secretKey,
      @Nullable SecretStore secretStore,
      @Nullable String verifyUrl,
      @Nullable ObjectMapper mapper,
      @Nullable HttpClient httpClient) {
    if (secretStore == null && secretKey == null) {
      throw new IllegalArgumentException("recaptcha secret required");
    }

    this.httpClient =
        httpClient != null
            ? httpClient
            : HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    this.secretKey =
        secretKey != null
            ? secretKey
            : secretStore
                .get("RECAPTCHA_SECRET_KEY")
                .orElseThrow(() -> new IllegalArgumentException("recaptcha site key required"));

    this.domain = domain;
    this.mapper = mapper != null ? mapper : new ObjectMapper();
    this.verifyUrl =
        verifyUrl != null ? verifyUrl : "https://www.google.com/recaptcha/api/siteverify";
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
}
