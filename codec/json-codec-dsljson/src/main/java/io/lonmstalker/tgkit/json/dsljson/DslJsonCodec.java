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
 * <p>Кеширует {@link JsonReader.ReadObject} для ускорения десериализации.</p>
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
  public <T> @NonNull T fromBytes(@NonNull byte[] data, @NonNull Class<T> type)
      throws IOException {
    JsonReader.ReadObject<T> reader =
        (JsonReader.ReadObject<T>) READERS.computeIfAbsent(type, JSON::tryFindReader);
    if (reader != null) {
      JsonReader<Object> r = JSON.newReader(data);
      return reader.read(r);
    }
    return JSON.deserialize(type, data, data.length);
  }
}
