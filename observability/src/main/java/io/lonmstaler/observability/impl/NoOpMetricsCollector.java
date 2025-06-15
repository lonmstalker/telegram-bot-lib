package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.MetricsCollector;
import io.lonmstaler.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.noop.NoopCounter;
import io.micrometer.core.instrument.noop.NoopMeterRegistry;
import io.micrometer.core.instrument.noop.NoopTimer;
import io.micrometer.core.instrument.Timer;

public final class NoOpMetricsCollector implements MetricsCollector {
    private static final MeterRegistry REGISTRY = new NoopMeterRegistry();
    private static final Counter COUNTER = new NoopCounter(REGISTRY);
    private static final Timer TIMER = new NoopTimer(REGISTRY);

    @Override
    public MeterRegistry registry() {
        return REGISTRY;
    }

    @Override
    public Timer timer(String name, Tags tags) {
        return TIMER;
    }

    @Override
    public Counter counter(String name, Tags tags) {
        return COUNTER;
    }
}
