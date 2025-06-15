package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.MetricsCollector;
import io.lonmstaler.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.HTTPServer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public final class MicrometerCollector implements MetricsCollector {
    private final MeterRegistry registry;

    public MicrometerCollector(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public @NonNull MeterRegistry registry() {
        return registry;
    }

    @Override
    public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
        return Timer.builder(name).tags(List.of(tags.items())).register(registry);
    }

    @Override
    public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
        return Counter.builder(name).tags(List.of(tags.items())).register(registry);
    }

    public static MicrometerCollector prometheus(int port) {
        PrometheusMeterRegistry reg = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        try {
            new HTTPServer(port, reg.getPrometheusRegistry());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new MicrometerCollector(reg);
    }
}
