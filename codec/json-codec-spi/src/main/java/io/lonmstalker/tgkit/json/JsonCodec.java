package io.lonmstalker.tgkit.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * SPI для сериализации/десериализации JSON.
 */
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
