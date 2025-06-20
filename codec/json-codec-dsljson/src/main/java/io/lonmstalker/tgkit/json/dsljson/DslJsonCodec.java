package io.lonmstalker.tgkit.json.dsljson;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import io.lonmstalker.tgkit.json.JsonCodec;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** JSON codec на основе библиотеки DSL-JSON. */
public final class DslJsonCodec implements JsonCodec {

  private final DslJson<Object> json =
      new DslJson<>(new DslJson.Settings<Object>().includeServiceLoader());
  private final Map<Class<?>, JsonReader.ReadObject<?>> readers = new ConcurrentHashMap<>();

  @Override
  @SuppressWarnings("unchecked")
  public <T> T fromBytes(byte[] data, Class<T> type) {
    if (data == null || type == null) {
      throw new IllegalArgumentException("data and type must not be null");
    }
    JsonReader.ReadObject<T> reader =
        (JsonReader.ReadObject<T>)
            readers.computeIfAbsent(type, t -> json.tryFindReader(type));
    if (reader == null) {
      throw new IllegalArgumentException("No reader for type: " + type);
    }
    JsonReader<Object> jr = json.newReader(data, data.length);
    try {
      return reader.read(jr);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public byte[] toBytes(Object value) {
    if (value == null) {
      throw new IllegalArgumentException("value must not be null");
    }
    JsonWriter writer = json.newWriter();
    json.serialize(writer, value);
    return writer.toByteArray();
  }
}
