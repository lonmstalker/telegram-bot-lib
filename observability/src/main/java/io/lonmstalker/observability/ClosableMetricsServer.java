package io.lonmstalker.observability;

import java.io.IOException;

/**
 * Интерфейс для управляемого HTTP-сервера метрик.
 */
public interface ClosableMetricsServer {

    /**
     * Запустить сервер метрик, принимающий запросы Prometheus.
     */
    void start() throws IOException;

    /**
     * Остановить сервер метрик.
     */
    void stop() throws IOException;
}