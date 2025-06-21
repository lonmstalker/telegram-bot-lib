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

/** Интерфейс тега метрики. */
public interface Tag extends Comparable<Tag> {

  /**
   * Создаёт неизменяемый тег.
   *
   * @param key ключ
   * @param value значение
   * @return новый тег
   */
  static Tag of(@NonNull String key, @NonNull String value) {
    return new ImmutableTag(key, value);
  }

  /** Возвращает ключ тега. */
  @NonNull String key();

  /** Возвращает значение тега. */
  @NonNull String value();

  /**
   * Сравнивает теги по ключу.
   *
   * @param o другой тег
   * @return результат сравнения
   */
  @Override
  default int compareTo(@NonNull Tag o) {
    return this.key().compareTo(o.key());
  }
}
