package io.lonmstaler.observability;

import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.interceptor.BotInterceptor;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ObservabilityInterceptor implements BotInterceptor {
    private final MetricsCollector metrics;
    private final Tracer tracer;
    private final ThreadLocal<Timer.Sample> samples = new ThreadLocal<>();
    private final ThreadLocal<Span> spans = new ThreadLocal<>();

    public ObservabilityInterceptor(@NonNull MetricsCollector metrics, @NonNull Tracer tracer) {
        this.metrics = metrics;
        this.tracer = tracer;
    }

    @Override
    public void preHandle(@NonNull Update update) {
        Tags tags = Tags.of(Tag.of("type", String.valueOf(update.hasMessage())));
        samples.set(Timer.start(metrics.registry()));
        spans.set(tracer.start("update", Attributes.of(AttributeKey.longKey("id"), update.getUpdateId())));
        LogContext.put("updateId", String.valueOf(update.getUpdateId()));
    }

    @Override
    public void postHandle(@NonNull Update update) {
        // nothing
    }

    @Override
    public void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) {
        Tags tags = Tags.of(Tag.of("type", String.valueOf(update.hasMessage())));
        Timer.Sample s = samples.get();
        if (s != null) {
            s.stop(metrics.timer("update_latency_ms", tags));
            samples.remove();
        }
        Span span = spans.get();
        if (span != null) {
            if (ex != null) {
                span.setError(ex);
            }
            span.close();
            spans.remove();
        }
        if (ex != null) {
            metrics.counter("errors_total", tags).increment();
        }
        metrics.counter("updates_total", tags).increment();
        LogContext.clear();
    }
}
