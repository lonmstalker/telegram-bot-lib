package io.lonmstalker.tgkit.observability;

import java.io.IOException;

/**
 * Интерфейс для управляемого HTTP-сервера метрик.
 */
public interface ClosableMetricsServer extends AutoCloseable {

    /**
     * Запустить сервер метрик, принимающий запросы Prometheus.
     */
    void start() throws IOException;

    /**
     * Остановить сервер метрик.
     */
    void stop() throws IOException;
}