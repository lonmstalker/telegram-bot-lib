package io.lonmstaler.observability.impl;

import io.lonmstaler.observability.MetricsCollector;
import io.lonmstaler.observability.Tags;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.HTTPServer;

public final class MicrometerCollector implements MetricsCollector {
    private final MeterRegistry registry;

    public MicrometerCollector(MeterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public MeterRegistry registry() {
        return registry;
    }

    @Override
    public Timer timer(String name, Tags tags) {
        return Timer.builder(name).tags(tags.items()).register(registry);
    }

    @Override
    public Counter counter(String name, Tags tags) {
        return Counter.builder(name).tags(tags.items()).register(registry);
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
