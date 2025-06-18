package io.lonmstalker.observability.impl;

import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Трассировщик-заглушка, не сохраняющий информацию о span'ах.
 */
public final class NoOpTracer implements Tracer {

    /**
     * Возвращает пустой {@link Span}, не совершающий никаких действий.
     */
    @Override
    public @NonNull Span start(@NonNull String spanName, @NonNull Tags tags) {
        return new Span() {
            @Override
            public void setError(@NonNull Throwable t) {
                // no-op
            }

            @Override
            public void setTag(@NonNull String tag, @NonNull String value) {
                // no-op
            }

            @Override
            public void close() {
                // no-op
            }
        };
    }
}
