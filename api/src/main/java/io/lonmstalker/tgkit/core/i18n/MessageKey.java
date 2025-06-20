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
package io.lonmstalker.tgkit.core.i18n;

import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Обёртка над ключом для локализованных сообщений и аргументами форматирования. */
public record MessageKey(String key, Object... args) {

  /**
   * @param key уникальный ключ в ресурсах (например, "wizard.reg.name.ask")
   * @param args параметры для {@link String#format(Locale, String, Object...)}
   */
  public MessageKey(@NonNull String key, @NonNull Object... args) {
    this.key = key;
    this.args = args != null ? args.clone() : new Object[0];
  }

  /**
   * @return аргументы для форматирования
   */
  @Override
  public Object[] args() {
    return args.clone();
  }

  public static @NonNull MessageKey of(@NonNull String key, Object... args) {
    return new MessageKey(key, args);
  }
}
