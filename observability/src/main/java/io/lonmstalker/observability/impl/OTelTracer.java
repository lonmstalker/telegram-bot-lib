package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.Span;
import io.lonmstalker.observability.Tags;
import io.lonmstalker.observability.Tracer;
import io.micrometer.core.instrument.Tag;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Реализация {@link Tracer} на базе OpenTelemetry.
 */
public final class OTelTracer implements Tracer {
    private final OpenTelemetrySdk sdk;
    private final io.opentelemetry.api.trace.Tracer tracer;

    private OTelTracer(io.opentelemetry.api.trace.Tracer tracer,
                       @NonNull OpenTelemetrySdk sdk) {
        this.sdk = sdk;
        this.tracer = tracer;
    }

    @Override
    public Span start(@NonNull String name, @NonNull Tags tags) {
        var span = tracer.spanBuilder(name);
        for (Tag item : tags.items()) {
            span.setAttribute(item.getKey(), item.getValue());
        }
        return new OtelSpan(span.startSpan());
    }

    /**
     * Полная остановка SDK (shutdown).
     */
    public void shutdown() {
        sdk.getSdkTracerProvider().shutdown();
    }

    /**
     * Билдер для гибкой настройки OTelTracer.
     */
    @SuppressWarnings("initialization.field.uninitialized")
    public static class Builder {
        private SpanExporter exporter;
        private String serviceName = "unknown-service";

        /**
         * Установить кастомный SpanExporter (Console, OTLP и т.д.).
         */
        public Builder withExporter(SpanExporter exporter) {
            this.exporter = exporter;
            return this;
        }

        /**
         * Установить имя сервиса (resource).
         */
        public Builder serviceName(String serviceName) {
            this.serviceName = Objects.requireNonNull(serviceName);
            return this;
        }

        public OTelTracer build() {
            SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                    .addSpanProcessor(SimpleSpanProcessor.create(
                            exporter != null ? exporter : SpanExporter.composite() // noop, если не задан
                    ))
                    .build();

            OpenTelemetrySdk sdk = OpenTelemetrySdk.builder()
                    .setTracerProvider(tracerProvider)
                    .build();

            var apiTracer = sdk.getTracer(serviceName);
            return new OTelTracer(apiTracer, sdk);
        }
    }

    /**
     * Обёртка над span OpenTelemetry.
     */
    private record OtelSpan(io.opentelemetry.api.trace.Span delegate) implements Span {

        @Override
        public void setError(@NonNull Throwable t) {
            delegate.recordException(t);
        }

        @Override
        public void setTag(@NonNull String tag, @NonNull String value) {
            delegate.setAttribute(tag, value);
        }

        @Override
        public void close() {
            delegate.end();
        }
    }
}
