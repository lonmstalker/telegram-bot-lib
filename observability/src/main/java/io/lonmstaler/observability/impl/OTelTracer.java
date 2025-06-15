package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.Span;
import io.lonmstaler.observability.Tracer;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.exporter.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;

public final class OTelTracer implements Tracer {
    private final io.opentelemetry.api.trace.Tracer tracer;

    public OTelTracer(TracerProvider provider, String serviceName) {
        this.tracer = provider.get(serviceName);
    }

    @Override
    public Span start(String name, Attributes attrs) {
        io.opentelemetry.api.trace.Span span = tracer.spanBuilder(name)
                .setAllAttributes(attrs)
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
        return new OtelSpan(span);
    }

    private record OtelSpan(io.opentelemetry.api.trace.Span delegate) implements Span {
        @Override
        public void setError(Throwable t) {
            delegate.recordException(t);
        }

        @Override
        public void close() {
            delegate.end();
        }
    }

    public static OTelTracer stdoutDev() {
        SpanExporter exporter = SpanExporter.composite(LoggingSpanExporter.create());
        SdkTracerProvider provider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(exporter))
                .build();
        return new OTelTracer(provider, "tg-bot");
    }
}
