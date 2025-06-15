package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.Span;
import io.lonmstaler.observability.Tracer;
import io.opentelemetry.api.common.Attributes;

/**
 * Трассировщик-заглушка, не сохраняющий информацию о span'ах.
 */
public final class NoOpTracer implements Tracer {
    @Override
    /**
     * Возвращает пустой {@link Span}, не совершающий никаких действий.
     */
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
