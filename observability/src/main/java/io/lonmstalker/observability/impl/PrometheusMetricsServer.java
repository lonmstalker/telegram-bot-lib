package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.ClosableMetricsServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import lombok.Builder;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Реализация ClosableMetricsServer на базе Prometheus HTTPServer.
 */
public class PrometheusMetricsServer implements ClosableMetricsServer {
    private final HTTPServer server;

    @Builder
    public PrometheusMetricsServer(int port, CollectorRegistry registry) throws IOException {
        // Создаём сервер без автоматического запуска
        server = new HTTPServer(new InetSocketAddress(port), registry, false);
    }

    @Override
    public void start() throws IOException {
        // none
    }

    @Override
    public void stop() {
        server.close();
    }

    @Override
    public void close() throws Exception {
        stop();
    }
}
