package io.lonmstalker.tgkit.core.exception;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Исключение, бросаемое при неуспешной валидации значения в шаге wizard.
 *
 * <p>Содержит {@link MessageKey} — ключ для вывода пользователю локализованного сообщения об
 * ошибке.
 */
@Getter
public class ValidationException extends RuntimeException {

  private final MessageKey errorKey;

  /**
   * @param errorKey ключ для локализованного сообщения об ошибке
   */
  public ValidationException(@NonNull MessageKey errorKey) {
    super(errorKey.key());
    this.errorKey = errorKey;
  }

  public static @NonNull ValidationException of(@NonNull MessageKey errorKey) {
    return new ValidationException(errorKey);
  }

  public static @NonNull ValidationException of(@NonNull String errorKey) {
    return new ValidationException(new MessageKey(errorKey));
  }
}
