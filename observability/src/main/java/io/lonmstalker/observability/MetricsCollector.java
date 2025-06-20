package io.lonmstalker.observability;

import io.lonmstalker.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Абстракция над системой сбора метрик. */
public interface MetricsCollector extends AutoCloseable {

  /** Возвращает используемый {@link MeterRegistry}. */
  @NonNull MeterRegistry registry();

  /**
   * Получает таймер с заданным именем и набором тегов.
   *
   * @param name имя метрики
   * @param tags набор тегов
   * @return таймер
   */
  @NonNull Timer timer(@NonNull String name, @NonNull Tags tags);

  /**
   * Получает счётчик с заданным именем и тегами.
   *
   * @param name имя счётчика
   * @param tags набор тегов
   * @return счётчик
   */
  @NonNull Counter counter(@NonNull String name, @NonNull Tags tags);
}
