package io.lonmstalker.tgkit.json;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Абстракция для сериализации и десериализации JSON.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * JsonCodec codec = ServiceLoader.load(JsonCodec.class).findFirst().orElseThrow();
 * byte[] bytes = codec.toBytes(obj);
 * MyType value = codec.fromBytes(bytes, MyType.class);
 * }</pre>
 */
public interface JsonCodec {

  /**
   * Преобразует JSON в объект указанного типа.
   *
   * @param json байты JSON в кодировке UTF-8
   * @param type класс целевого типа
   * @param <T>  тип результата
   * @return десериализованный объект
   */
  <T> @NonNull T fromBytes(@NonNull byte[] json, @NonNull Class<T> type);

  /**
   * Кодирует объект в JSON.
   *
   * @param value исходный объект
   * @return JSON в виде массива байт UTF-8
   */
  @NonNull byte[] toBytes(@NonNull Object value);
}
