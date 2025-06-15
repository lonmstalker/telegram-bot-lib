package io.lonmstalker.observability;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерцептор, собирающий метрики и трассировки для каждого обновления.
 */
public class ObservabilityInterceptor implements BotInterceptor {
    private static final ThreadLocal<Span> SPANS = new ThreadLocal<>();
    private static final ThreadLocal<Timer.Sample> SAMPLE = new ThreadLocal<>();
    private final MetricsCollector metrics;
    private final Tracer tracer;

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

    @Override
    /**
     * Начало обработки обновления: стартуем таймер и создаём span.
     *
     * @param update полученное обновление
     */
    public void preHandle(@NonNull Update update) {
        SAMPLE.set(Timer.start(metrics.registry()));
        SPANS.set(tracer.start("update", Attributes.of(AttributeKey.longKey("id"),
                update.getUpdateId().longValue())));
        LogContext.put("updateId", String.valueOf(update.getUpdateId()));
    }

    @Override
    /**
     * Завершающий этап после хендлера. В данной реализации ничего не делает.
     */
    public void postHandle(@NonNull Update update) {
        // nothing
    }

    @Override
    /**
     * Завершает обработку: фиксирует метрики и закрывает span.
     *
     * @param update   обработанное обновление
     * @param response сформированный ответ
     * @param ex       ошибка, если возникла
     */
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
            metrics.counter("errors_total", tags).increment();
        }
        metrics.counter("updates_total", tags).increment();
        LogContext.clear();
    }
}
