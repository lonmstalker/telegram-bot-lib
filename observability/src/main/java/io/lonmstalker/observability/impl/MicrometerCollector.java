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
import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Реализация {@link MetricsCollector} на базе Micrometer.
 */
public class MicrometerCollector implements MetricsCollector {
  private final MeterRegistry registry;
  private final PrometheusMetricsServer httpServer;
  private final ConcurrentMap<String, AtomicReference<Double>> gauges = new ConcurrentHashMap<>();

  public MicrometerCollector(
      @NonNull MeterRegistry registry, @NonNull PrometheusMetricsServer httpServer) {
    this.registry = registry;
    this.httpServer = httpServer;
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

  @Override
  public void gauge(@NonNull String name, @NonNull Tags tags, double value) {
    String key = name + map(tags).toString();
    AtomicReference<Double> ref =
        gauges.computeIfAbsent(
            key,
            k -> {
              AtomicReference<Double> r = new AtomicReference<>(value);
              Gauge.builder(name, r, AtomicReference::get).tags(map(tags)).register(registry);
              return r;
            });
    ref.set(value);
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
