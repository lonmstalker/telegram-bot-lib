package io.lonmstalker.observability.impl;

import io.lonmstalker.tgkit.observability.ClosableMetricsServer;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Реализация {@link ClosableMetricsServer} на базе Prometheus {@link HTTPServer}.
 *
 * <p>Конструктор сразу создаёт и запускает HTTPServer, поэтому {@link #start()} оставлен пустым.
 * Достаточно создать экземпляр этого класса и он начнёт принимать запросы на указанный порт.
 */
public class PrometheusMetricsServer implements ClosableMetricsServer {
  private final HTTPServer server;

  public PrometheusMetricsServer(int port, CollectorRegistry registry) throws IOException {
    // HTTPServer автоматически запускается внутри конструктора
    server = new HTTPServer(new InetSocketAddress(port), registry, false);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private int port;
    private CollectorRegistry registry;

    public Builder port(int port) {
      this.port = port;
      return this;
    }

    public Builder registry(CollectorRegistry registry) {
      this.registry = registry;
      return this;
    }

    public PrometheusMetricsServer build() throws IOException {
      return new PrometheusMetricsServer(port, registry);
    }
  }

  /** Метод ничего не делает, так как сервер запускается в конструкторе. */
  @Override
  public void start() throws IOException {
    // no-op
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
