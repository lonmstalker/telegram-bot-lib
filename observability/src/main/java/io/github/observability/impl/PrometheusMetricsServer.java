/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.observability.impl;

import io.github.tgkit.observability.ClosableMetricsServer;
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
}
