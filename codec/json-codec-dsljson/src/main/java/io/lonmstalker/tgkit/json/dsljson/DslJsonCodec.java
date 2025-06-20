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
package io.lonmstalker.tgkit.json.dsljson;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import io.lonmstalker.tgkit.json.JsonCodec;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Реализация {@link JsonCodec} на основе DSL-JSON.
 *
 * <p>Кеширует {@link JsonReader.ReadObject} для ускорения десериализации.
 */
public final class DslJsonCodec implements JsonCodec {

  private static final DslJson<Object> JSON = new DslJson<>();
  private static final ConcurrentMap<Class<?>, JsonReader.ReadObject<?>> READERS =
      new ConcurrentHashMap<>();

  @Override
  public byte[] toBytes(@NonNull Object value) {
    return JSON.serialize(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> @NonNull T fromBytes(@NonNull byte[] data, @NonNull Class<T> type) throws IOException {
    JsonReader.ReadObject<T> reader =
        (JsonReader.ReadObject<T>) READERS.computeIfAbsent(type, JSON::tryFindReader);
    if (reader != null) {
      JsonReader<Object> r = JSON.newReader(data);
      return reader.read(r);
    }
    return JSON.deserialize(type, data, data.length);
  }
}
