package io.lonmstalker.observability.impl;

import com.sun.net.httpserver.HttpServer;
import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tags;
import io.lonmstalker.core.exception.BotApiException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public record MicrometerCollector(MeterRegistry registry) implements MetricsCollector {

    @Override
    public @NonNull Timer timer(@NonNull String name, @NonNull Tags tags) {
        return Timer.builder(name).tags(List.of(tags.items())).register(registry);
    }

    @Override
    public @NonNull Counter counter(@NonNull String name, @NonNull Tags tags) {
        return Counter.builder(name).tags(List.of(tags.items())).register(registry);
    }

    public static @NonNull MicrometerCollector prometheus(int port) {
        PrometheusMeterRegistry reg = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/prometheus", httpExchange -> {
                String response = reg.scrape();
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });

            new Thread(server::start).start();
        } catch (IOException e) {
            throw new BotApiException(e);
        }
        return new MicrometerCollector(reg);
    }
}
