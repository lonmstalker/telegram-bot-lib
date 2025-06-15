package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.Span;
import io.lonmstalker.observability.Tracer;
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
