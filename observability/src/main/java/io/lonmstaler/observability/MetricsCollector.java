package io.lonmstaler.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

public interface MetricsCollector {
    MeterRegistry registry();
    Timer timer(String name, Tags tags);
    Counter counter(String name, Tags tags);
}
