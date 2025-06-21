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
package io.github.tgkit.internal;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Конвертер для преобразования {@link BotRequest} в произвольный тип, используемый обработчиком.
 *
 * @param <T> целевой тип после преобразования
 */
@FunctionalInterface
public interface BotHandlerConverter<T> {

  /**
   * Выполняет преобразование запроса.
   *
   * @param request исходный запрос
   * @return результат преобразования
   */
  @NonNull T convert(@NonNull BotRequest<?> request);

  /** Конвертер по умолчанию, возвращающий исходный запрос без изменений. */
  class Identity implements BotHandlerConverter<BotRequest<?>> {
    @Override
    public @NonNull BotRequest<?> convert(@NonNull BotRequest<?> request) {
      return request;
    }
  }
}
