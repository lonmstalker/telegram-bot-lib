/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import java.io.IOException;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Реализация {@link MetricsCollector} на базе Micrometer. */
public class MicrometerCollector implements MetricsCollector {
  private final MeterRegistry registry;
  private final PrometheusMetricsServer httpServer;

  public MicrometerCollector(
      @NonNull MeterRegistry registry, @NonNull PrometheusMetricsServer httpServer) {
    this.registry = registry;
    this.httpServer = httpServer;
  }

  @Override
  public @NonNull MeterRegistry registry() {
    return registry;
  }

  @Override
  public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
    return Timer.builder(name).tags(map(tags)).register(registry);
  }

  @Override
  public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
    return Counter.builder(name).tags(map(tags)).register(registry);
  }

  /**
   * Создаёт collector и поднимает HTTP-сервер для экспонирования метрик в формате Prometheus.
   *
   * @param port порт HTTP-сервера
   * @return созданный collector
   */
  public static @NonNull MicrometerCollector prometheus(PrometheusConfig cfg, int port) {
    PrometheusMeterRegistry reg = new PrometheusMeterRegistry(cfg);
    try {
      return new MicrometerCollector(
          reg, new PrometheusMetricsServer(port, new CollectorRegistry()));
    } catch (IOException e) {
      throw new BotApiException(e);
    }
  }

  @Override
  public void close() {
    registry.close();
    httpServer.stop();
  }

  private io.micrometer.core.instrument.Tags map(@NonNull Tags tags) {
    return io.micrometer.core.instrument.Tags.of(
        Arrays.stream(tags.items()).map(t -> Tag.of(t.key(), t.value())).toList());
  }
}
