package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Заглушка MetricsCollector, не сохраняющая метрики. */
public class NoOpMetricsCollector implements MetricsCollector {
  private final MeterRegistry registry = new SimpleMeterRegistry();

  @Override
  public @NonNull MeterRegistry registry() {
    return registry;
  }

  @Override
  public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
    return Timer.builder(name).register(registry);
  }

  @Override
  public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
    return Counter.builder(name).register(registry);
  }

  @Override
  public void gauge(@NonNull String name, @NonNull Tags tags, double value) {
    // no-op
  }

  @Override
  public void close() {
    registry.close();
  }
}
