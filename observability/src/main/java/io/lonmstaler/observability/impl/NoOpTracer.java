package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.Span;
import io.lonmstaler.observability.Tracer;
import io.opentelemetry.api.common.Attributes;

public final class NoOpTracer implements Tracer {
    @Override
    public Span start(String spanName, Attributes attributes) {
        return new Span() {
            @Override
            public void setError(Throwable t) {
                // no-op
            }

            @Override
            public void close() {
                // no-op
            }
        };
    }
}
