package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.MetricsCollector;
import io.lonmstaler.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopMeterRegistry;
import io.micrometer.core.instrument.noop.NoopTimer;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class NoOpMetricsCollector implements MetricsCollector {
    private static final MeterRegistry REGISTRY = new NoopMeterRegistry();
    private static final Counter COUNTER = new NoopCounter(REGISTRY);
    private static final Timer TIMER = new NoopTimer(REGISTRY);

    @Override
    public @NonNull MeterRegistry registry() {
        return REGISTRY;
    }

    @Override
    public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
        return TIMER;
    }

    @Override
    public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
        return COUNTER;
    }
}
