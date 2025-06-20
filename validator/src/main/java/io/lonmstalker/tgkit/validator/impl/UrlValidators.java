package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import io.lonmstalker.tgkit.validator.moderation.ContentModerationService;
import java.net.URI;
import java.util.ServiceLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Валидаторы для URL (WebAppData или ссылки).
 *
 * <p>Проверяют синтаксис URI и безопасность через Safe Browsing.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UrlValidators {

  private static final ContentModerationService MOD =
      ServiceLoader.load(ContentModerationService.class).findFirst().orElse(null);

  /**
   * Проверяет, что строка является корректным URI.
   *
   * @return Validator<String> с ключом "error.url.syntax"
   */
  public static Validator<@NonNull String> validUri() {
    return Validator.of(
        s -> {
          try {
            URI.create(s);
            return true;
          } catch (Exception e) {
            return false;
          }
        },
        MessageKey.of("error.url.syntax"));
  }

  /**
   * Проверяет безопасность URL через Google Safe Browsing.
   *
   * @return Validator<String> с ключом "error.url.unsafe"
   */
  public static Validator<@NonNull String> safeBrowsing() {
    return Validator.of(s -> MOD == null || MOD.isUrlSafe(s), MessageKey.of("error.url.unsafe"));
  }
}
