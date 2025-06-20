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
package io.lonmstalker.tgkit.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.checkerframework.checker.nullness.qual.NonNull;

/** SPI для сериализации/десериализации JSON. */
public interface JsonCodec {
  /** Возвращает JSON представление объекта в виде массива байт. */
  byte[] toBytes(@NonNull Object value) throws IOException;

  /**
   * Создаёт объект из JSON.
   *
   * @param data JSON байты
   * @param type тип целевого объекта
   * @param <T> тип
   * @return результат десериализации
   * @throws IOException при ошибке парсинга
   */
  @NonNull <T> T fromBytes(@NonNull byte[] data, @NonNull Class<T> type) throws IOException;

  /** Возвращает JSON строку. */
  default @NonNull String toString(@NonNull Object value) throws IOException {
    return new String(toBytes(value), StandardCharsets.UTF_8);
  }

  /** Создаёт объект из JSON строки. */
  default <T> @NonNull T fromString(@NonNull String json, @NonNull Class<T> type)
      throws IOException {
    return fromBytes(json.getBytes(StandardCharsets.UTF_8), type);
  }
}
