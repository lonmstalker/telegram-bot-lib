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
package io.lonmstalker.tgkit.core.exception;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Исключение, бросаемое при неуспешной валидации значения в шаге wizard.
 *
 * <p>Содержит {@link MessageKey} — ключ для вывода пользователю локализованного сообщения об
 * ошибке.
 */
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

  public MessageKey getErrorKey() {
    return errorKey;
  }
}
