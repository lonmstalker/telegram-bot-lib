package io.lonmstalker.observability;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.update.UpdateUtils;
import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tag;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Интерцептор, собирающий метрики и трассировки для каждого обновления. */
public class ObservabilityInterceptor implements BotInterceptor {
  private final Tracer tracer;
  private final MetricsCollector metrics;
  private final ThreadLocal<Span> SPANS = new ThreadLocal<>();
  private final ThreadLocal<Timer.Sample> SAMPLE = new ThreadLocal<>();

  /**
   * Создаёт интерцептор.
   *
   * @param metrics сборщик метрик
   * @param tracer трассировщик
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

  /** Завершающий этап после хендлера. В данной реализации ничего не делает. */
  @Override
  public void postHandle(@NonNull Update update, @NonNull BotRequest<?> request) {
    // nothing
  }

  /** Завершает обработку: фиксирует метрики и закрывает span. */
  @Override
  public void afterCompletion(
      @NonNull Update update,
      @Nullable BotRequest<?> request,
      @Nullable BotResponse response,
      @Nullable Exception ex) {
    Tags tags = Tags.of(Tag.of("type", UpdateUtils.getType(update).name()));
    Timer.Sample s = SAMPLE.get();
    if (s != null) {
      s.stop(metrics.timer("update_latency_ms", tags));
      SAMPLE.remove();
    }
    Span span = SPANS.get();
    if (span != null) {
      if (ex != null) {
        span.setError(ex);
      }
      span.close();
      SPANS.remove();
    }
    if (ex != null) {
      metrics.counter("update_errors_total", tags).increment();
    }
    metrics.counter("updates_total", tags).increment();
    LogContext.clear();
  }
}
