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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * SPI для сериализации и десериализации объектов в JSON.
 *
 * <p>Интерфейс абстрагирует выбранный JSON‑парсер, позволяя подключать
 * различные реализации (Jackson, Gson и др.).
 * Пример использования:
 *
 * <pre>{@code
 * JsonCodec codec = new JacksonJsonCodec();
 * try (OutputStream out = Files.newOutputStream(Path.of("data.json"))) {
 *   codec.serialize(data, out);
 * }
 * try (InputStream in = Files.newInputStream(Path.of("data.json"))) {
 *   MyDto dto = codec.deserialize(in, MyDto.class);
 * }
 * }
 * </pre>
 */
public interface JsonCodec {
  /** Сериализует объект в поток. */
  <T> void serialize(T obj, OutputStream out) throws Exception;

  /** Читает объект указанного типа из потока. */
  <T> T deserialize(InputStream in, Class<T> type) throws Exception;
}
