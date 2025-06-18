package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
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
        return Timer.builder(name)
                .tags(map(tags))
                .register(registry);
    }

    @Override
    public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
        return Counter.builder(name)
                .tags(map(tags))
                .register(registry);
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

    private io.micrometer.core.instrument.Tags map(@NonNull Tags tags) {
        return io.micrometer.core.instrument.Tags.of(
                Arrays.stream(tags.items())
                        .map(t -> Tag.of(t.key(), t.value()))
                        .toList()
        );
    }
}
