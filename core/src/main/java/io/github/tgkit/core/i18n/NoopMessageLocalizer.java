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
package io.github.tgkit.core.i18n;

import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Заглушка {@link MessageLocalizer}, возвращающая ключ без изменений.
 *
 * <p>Полезно, когда поддержка локализации не нужна. Пример использования:
 *
 * <pre>{@code
 * MessageLocalizer localizer = new NoopMessageLocalizer();
 * String text = localizer.get("hello.world"); // "hello.world"
 * }</pre>
 */
public class NoopMessageLocalizer implements MessageLocalizer {

  /** Устанавливает текущую локаль. Значение игнорируется. */
  @Override
  public void setLocale(@NonNull Locale locale) {
    // no-op
  }

  /** Сбрасывает локаль к умолчанию. Ничего не делает. */
  @Override
  public void resetLocale() {
    // no-op
  }

  /** Возвращает переданный ключ без изменений. */
  @Override
  public @NonNull String get(@NonNull MessageKey key) {
    return key.key();
  }

  /** Возвращает переданный ключ без изменений. */
  @Override
  public @NonNull String get(@NonNull String key) {
    return key;
  }

  /** Возвращает указанное значение по умолчанию. */
  @Override
  public @NonNull String get(@NonNull String key, @NonNull String defaultValue) {
    return defaultValue;
  }

  /** Возвращает ключ, игнорируя аргументы форматирования. */
  @Override
  public @NonNull String get(@NonNull String key, @NonNull Object... args) {
    return key;
  }

  /** Возвращает значение по умолчанию, не учитывая аргументы. */
  @Override
  public @NonNull String get(@NonNull String key, @NonNull String defaultValue, Object... args) {
    return defaultValue;
  }
}
