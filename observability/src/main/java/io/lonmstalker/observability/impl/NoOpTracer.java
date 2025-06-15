package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.Span;
import io.lonmstalker.observability.Tracer;
import io.opentelemetry.api.common.Attributes;

/**
 * Трассировщик-заглушка, не сохраняющий информацию о span'ах.
 */
public final class NoOpTracer implements Tracer {
    @Override
    /**
     * Возвращает пустой {@link Span}, не совершающий никаких действий.
     */
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
