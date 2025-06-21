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
package io.github.tgkit.observability;

import org.checkerframework.checker.nullness.qual.NonNull;

/** Интерфейс абстракции над span системы трассировки. */
public interface Span extends AutoCloseable {

  /**
   * Отмечает span как завершившийся с ошибкой.
   *
   * @param t причина ошибки
   */
  void setError(@NonNull Throwable t);

  /**
   * Проставляет тег в span
   *
   * @param tag - ключ
   * @param value - значение
   */
  void setTag(@NonNull String tag, @NonNull String value);

  /**
   * Завершает span без ошибки.
   *
   * @see AutoCloseable#close()
   */
  @Override
  void close();
}
