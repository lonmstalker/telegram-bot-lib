package io.lonmstalker.observability;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.micrometer.core.instrument.Tag;
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
    private final ThreadLocal<Span> SPANS = new ThreadLocal<>();
    private final ThreadLocal<Timer.Sample> SAMPLE = new ThreadLocal<>();

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
    public void preHandle(@NonNull Update update) {
        SAMPLE.set(Timer.start(metrics.registry()));
        SPANS.set(tracer.start("update", Tags.of(
                Tag.of("id", String.valueOf(update.getUpdateId())))
        ));
        LogContext.put("updateId", String.valueOf(update.getUpdateId()));
    }

    /**
     * Завершающий этап после хендлера. В данной реализации ничего не делает.
     */
    @Override
    public void postHandle(@NonNull Update update) {
        // nothing
    }

    /**
     * Завершает обработку: фиксирует метрики и закрывает span.
     *
     * @param update   обработанное обновление
     * @param response сформированный ответ
     * @param ex       ошибка, если возникла
     */
    @Override
    public void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) {
        Tags tags = Tags.of(Tag.of("type", String.valueOf(update.hasMessage())));
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
