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

public interface MessageLocalizer {

  /**
   * Устанавливает локаль для текущего потока.
   *
   * @param locale новая локаль
   */
  void setLocale(@NonNull Locale locale);

  /**
   * Сбрасывает локаль текущего потока на дефолтную.
   */
  void resetLocale();

  /**
   * Получить локализованную строку по ключу. Если ключ не найден, возвращается сам ключ.
   */
  @NonNull
  String get(@NonNull MessageKey key);

  /**
   * Получить локализованную строку по ключу. Если ключ не найден, возвращается сам ключ.
   */
  @NonNull
  String get(@NonNull String key);

  /**
   * Получить локализованную строку по ключу. Если ключ не найден, возвращается сам ключ.
   */
  @NonNull
  String get(@NonNull String key, @NonNull String defaultValue);

  /**
   * Получить локализованную и форматированную строку по ключу с параметрами.
   */
  @NonNull
  String get(@NonNull String key, @NonNull Object... args);

  /**
   * Получить локализованную и форматированную строку по ключу с параметрами.
   */
  @NonNull
  String get(@NonNull String key, @NonNull String defaultValue, Object... args);
}
