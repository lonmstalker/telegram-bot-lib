package io.lonmstalker.observability;

import io.lonmstalker.observability.impl.CompositeTracer;
import io.lonmstalker.observability.impl.MicrometerCollector;
import io.lonmstalker.observability.impl.NoOpTracer;
import io.lonmstalker.observability.impl.OTelTracer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Arrays;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotObservability {

    public static @NonNull Tracer noopTracer() {
        return new NoOpTracer();
    }

    public static @NonNull Tracer otelTracer(@NonNull String serviceName) {
        return new OTelTracer.Builder().serviceName(serviceName).build();
    }

    public static @NonNull Tracer compositeTracer(@NonNull Tracer... tracers) {
        return new CompositeTracer(Arrays.asList(tracers));
    }

    public static @NonNull MetricsCollector micrometer(int port) {
        return MicrometerCollector.prometheus(PrometheusConfig.DEFAULT, port);
    }

    public static @NonNull MetricsCollector micrometer(@NonNull PrometheusConfig cfg, int port) {
        return MicrometerCollector.prometheus(cfg, port);
    }
}
