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

package io.github.observability;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.interceptor.BotInterceptor;
import io.github.tgkit.core.update.UpdateUtils;
import io.github.tgkit.observability.Span;
import io.github.tgkit.observability.Tag;
import io.github.tgkit.observability.Tags;
import io.github.tgkit.observability.Tracer;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерцептор, собирающий метрики и трассировки для каждого обновления.
 */
public class ObservabilityInterceptor implements BotInterceptor {
  private final Tracer tracer;
  private final MetricsCollector metrics;
  private final ThreadLocal<Span> SPANS = ThreadLocal.withInitial(() -> null);
  private final ThreadLocal<Timer.Sample> SAMPLE = ThreadLocal.withInitial(() -> null);

  /**
   * Создаёт интерцептор.
   *
   * @param metrics сборщик метрик
   * @param tracer  трассировщик
   */
  public ObservabilityInterceptor(@NonNull MetricsCollector metrics, @NonNull Tracer tracer) {
    this.metrics = metrics;
    this.tracer = tracer;
  }

  /**
   * Начало обработки обновления: стартуем таймер и создаём span.
   *
   * @param update полученное обновление
   */
  @Override
  public void preHandle(@NonNull Update update, @NonNull BotRequest<?> request) {
    SAMPLE.set(Timer.start(metrics.registry()));
    SPANS.set(tracer.start("update", Tags.of(Tag.of("id", String.valueOf(update.getUpdateId())))));
    LogContext.put("updateId", String.valueOf(update.getUpdateId()));
  }

  /**
   * Завершающий этап после хендлера. В данной реализации ничего не делает.
   */
  @Override
  public void postHandle(@NonNull Update update, @NonNull BotRequest<?> request) {
    // nothing
  }

  /**
   * Завершает обработку: фиксирует метрики и закрывает span.
   */
  @Override
  public void afterCompletion(
      @NonNull Update update,
      @Nullable BotRequest<?> request,
      @Nullable BotResponse response,
      @Nullable Exception ex) {
    Tags tags = Tags.of(Tag.of("type", UpdateUtils.getType(update).name()));
    Timer.Sample sample = SAMPLE.get();
    Span span = SPANS.get();
    try {
      if (sample != null) {
        sample.stop(metrics.timer("update_latency_ms", tags));
      }
      if (span != null) {
        if (ex != null) {
          span.setError(ex);
        }
        span.close();
      }
      if (ex != null) {
        metrics.counter("update_errors_total", tags).increment();
      }
      metrics.counter("updates_total", tags).increment();
    } finally {
      SAMPLE.remove();
      SPANS.remove();
      LogContext.clear();
    }
  }
}
