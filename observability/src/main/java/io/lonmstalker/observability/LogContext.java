package io.lonmstalker.observability;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.MDC;

/** Утилитарный класс для работы с MDC при логировании. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogContext {

  /**
   * Помещает значение в контекст логирования.
   *
   * @param key ключ MDC
   * @param value значение
   */
  public static void put(@NonNull String key, @NonNull String value) {
    MDC.put(key, value);
  }

  /** Очищает MDC. */
  public static void clear() {
    MDC.clear();
  }

  /** Удаляет значение из MDC. */
  public static void remove(@NonNull String key) {
    MDC.remove(key);
  }
}
