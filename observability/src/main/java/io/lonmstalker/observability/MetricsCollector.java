package io.lonmstalker.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface MetricsCollector {
    @NonNull
    MeterRegistry registry();

    @NonNull
    Timer timer(@NonNull String name, @NonNull Tags tags);

    @NonNull
    Counter counter(@NonNull String name, @NonNull Tags tags);
}
