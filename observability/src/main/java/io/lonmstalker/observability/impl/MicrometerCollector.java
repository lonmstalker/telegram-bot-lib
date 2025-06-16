package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tags;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.client.CollectorRegistry;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Arrays;

/**
 * Реализация {@link MetricsCollector} на базе Micrometer.
 */
@AllArgsConstructor
public class MicrometerCollector implements MetricsCollector {
    private final MeterRegistry registry;
    private final PrometheusMetricsServer httpServer;

    @Override
    public @NonNull MeterRegistry registry() {
        return registry;
    }

    @Override
    public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
        return Timer.builder(name).tags(Arrays.asList(tags.items())).register(registry);
    }

    @Override
    public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
        return Counter.builder(name).tags(Arrays.asList(tags.items())).register(registry);
    }

    /**
     * Создаёт collector и поднимает HTTP-сервер для экспонирования метрик в формате Prometheus.
     *
     * @param port порт HTTP-сервера
     * @return созданный collector
     */
    public static @NonNull MicrometerCollector prometheus(PrometheusConfig cfg, int port) {
        PrometheusMeterRegistry reg = new PrometheusMeterRegistry(cfg);
        try {
            return new MicrometerCollector(reg, new PrometheusMetricsServer(port, new CollectorRegistry()));
        } catch (IOException e) {
            throw new BotApiException(e);
        }
    }

    @Override
    public void close() {
        registry.close();
        httpServer.stop();
    }
}
