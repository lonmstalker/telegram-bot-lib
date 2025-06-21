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
package io.github.observability.impl;

import io.github.observability.MetricsCollector;
import io.github.tgkit.observability.Tags;
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
