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
package io.lonmstalker.tgkit.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.impl.NoOpMetricsCollector;
import io.lonmstalker.tgkit.core.dsl.MissingIdStrategy;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.event.BotEventBus;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import io.lonmstalker.tgkit.webhook.WebhookServer;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Настройки по умолчанию для BotResponse. */
public class BotGlobalConfig {
  private static final Logger log = LoggerFactory.getLogger(BotGlobalConfig.class);

  /** Глобальная конфигурация. */
  public static final BotGlobalConfig INSTANCE = new BotGlobalConfig();

  private final @NonNull DSLGlobalConfig dslGlobalConfig;
  private final @NonNull HttpGlobalConfig httpGlobalConfig;
  private final @NonNull EventGlobalConfig eventGlobalConfig;
  private final @NonNull ExecutorsGlobalConfig executorsGlobalConfig;
  private final @NonNull WebhookGlobalConfig webhookGlobalConfig;
  private final @NonNull ObservabilityGlobalConfig observabilityGlobalConfig;

  private BotGlobalConfig() {
    this.executorsGlobalConfig = new ExecutorsGlobalConfig();

    this.dslGlobalConfig = new DSLGlobalConfig();
    this.httpGlobalConfig = new HttpGlobalConfig();
    this.eventGlobalConfig = new EventGlobalConfig();
    this.webhookGlobalConfig = new WebhookGlobalConfig();
    this.observabilityGlobalConfig = new ObservabilityGlobalConfig();

    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  eventGlobalConfig.close();
                  httpGlobalConfig.close();
                  webhookGlobalConfig.close();
                  dslGlobalConfig.close();
                  executorsGlobalConfig.close();
                  observabilityGlobalConfig.close();
                }));
  }

  public @NonNull EventGlobalConfig events() {
    return eventGlobalConfig;
  }

  public @NonNull DSLGlobalConfig dsl() {
    return this.dslGlobalConfig;
  }

  public @NonNull ExecutorsGlobalConfig executors() {
    return this.executorsGlobalConfig;
  }

  public @NonNull HttpGlobalConfig http() {
    return this.httpGlobalConfig;
  }

  public @NonNull WebhookGlobalConfig webhook() {
    return this.webhookGlobalConfig;
  }

  public @NonNull ObservabilityGlobalConfig observability() {
    return this.observabilityGlobalConfig;
  }

  public static class EventGlobalConfig {
    private final @NonNull AtomicReference<BotEventBus> eventBus = new AtomicReference<>();

    public @NonNull BotEventBus getBus() {
      return this.eventBus.get();
    }

    public @NonNull EventGlobalConfig bus(@NonNull BotEventBus eventBus) {
      log.debug("[core-init] BotEventBus changed to {}", eventBus.getClass().getSimpleName());
      this.eventBus.set(eventBus);
      return this;
    }

    void close() {
      try {
        this.eventBus.get().shutdown();
      } catch (InterruptedException ignored) {
      }
    }
  }

  public static class HttpGlobalConfig {
    private final @NonNull AtomicReference<@NonNull HttpClient> client = new AtomicReference<>();
    private final @NonNull AtomicReference<ObjectMapper> mapper = new AtomicReference<>();

    public @NonNull HttpGlobalConfig httpClient(@NonNull HttpClient httpClient) {
      log.debug("[core-init] HttpClient changed to {}", httpClient.getClass().getSimpleName());
      this.client.set(httpClient);
      return this;
    }

    public @NonNull HttpGlobalConfig mapper(@NonNull ObjectMapper mapper) {
      log.debug("[core-init] ObjectMapper changed to {}", mapper.getClass().getSimpleName());
      this.mapper.set(mapper);
      return this;
    }

    public @NonNull HttpClient getClient() {
      return this.client.get();
    }

    public @NonNull HttpClient getClient(@Nullable String proxyHost, int proxyPort) {
      if (proxyHost == null || proxyHost.isEmpty()) {
        return client.get();
      }
      return HttpClient.newBuilder()
          .connectTimeout(client.get().connectTimeout().orElse(Duration.ofSeconds(30)))
          .proxy(ProxySelector.of(new InetSocketAddress(proxyHost, proxyPort)))
          .build();
    }

    public @NonNull ObjectMapper getMapper() {
      return this.mapper.get();
    }

    void close() {
      client.get().close();
    }
  }

  public static class ExecutorsGlobalConfig {
    private final @NonNull AtomicReference<ScheduledExecutorService> scheduledExecutorService =
        new AtomicReference<>();
    private final @NonNull AtomicReference<ExecutorService> ioExecutorService =
        new AtomicReference<>();
    private final @NonNull AtomicReference<ExecutorService> cpuExecutorService =
        new AtomicReference<>();
    private final AtomicReference<Integer> scheduledPoolSize = new AtomicReference<>(2);
    private final AtomicReference<Integer> cpuPoolSize = new AtomicReference<>(2);

    public BotGlobalConfig.@NonNull ExecutorsGlobalConfig scheduledExecutorService(
        @NonNull ScheduledExecutorService scheduledExecutorService) {
      log.debug(
          "[core-init] ScheduledExecutorService changed to {}",
          scheduledExecutorService.getClass().getSimpleName());
      this.scheduledExecutorService.set(scheduledExecutorService);
      return this;
    }

    public BotGlobalConfig.@NonNull ExecutorsGlobalConfig ioExecutorService(
        @NonNull ExecutorService ioExecutor) {
      log.debug(
          "[core-init] IOExecutorService changed to {}", ioExecutor.getClass().getSimpleName());
      this.ioExecutorService.set(ioExecutor);
      return this;
    }

    public BotGlobalConfig.@NonNull ExecutorsGlobalConfig cpuExecutorService(
        @NonNull ExecutorService cpuExecutor) {
      log.debug(
          "[core-init] CpuExecutorService changed to {}", cpuExecutor.getClass().getSimpleName());
      this.cpuExecutorService.set(cpuExecutor);
      return this;
    }

    /**
     * Размер пула для CPU-задач. Используется при инициализации {@link
     * java.util.concurrent.ExecutorService} c виртуальными потоками.
     *
     * @param size желаемое количество потоков
     * @return конфигурация executors
     */
    public BotGlobalConfig.@NonNull ExecutorsGlobalConfig cpuPoolSize(int size) {
      this.cpuPoolSize.set(size);
      return this;
    }

    /**
     * Размер пула планировщика. Определяет количество виртуальных потоков в {@link
     * java.util.concurrent.ScheduledExecutorService}.
     *
     * @param size желаемое количество потоков
     * @return конфигурация executors
     */
    public BotGlobalConfig.@NonNull ExecutorsGlobalConfig scheduledPoolSize(int size) {
      this.scheduledPoolSize.set(size);
      return this;
    }

    /** Возвращает размер пула для CPU-задач. */
    public int cpuPoolSize() {
      return this.cpuPoolSize.get();
    }

    /** Возвращает размер пула планировщика. */
    public int scheduledPoolSize() {
      return this.scheduledPoolSize.get();
    }

    public @NonNull ScheduledExecutorService getScheduledExecutorService() {
      return this.scheduledExecutorService.get();
    }

    public @NonNull ExecutorService getIoExecutorService() {
      return this.ioExecutorService.get();
    }

    public @NonNull ExecutorService getCpuExecutorService() {
      return this.cpuExecutorService.get();
    }

    void close() {
      ioExecutorService.get().shutdown();
      scheduledExecutorService.get().shutdownNow();
    }
  }

  public static class DSLGlobalConfig {
    private final AtomicBoolean sanitize = new AtomicBoolean();
    private final @NonNull AtomicReference<FeatureFlags> flags = new AtomicReference<>();
    private final @NonNull AtomicReference<ParseMode> parseMode = new AtomicReference<>();
    private final @NonNull AtomicReference<TtlScheduler> ttlScheduler = new AtomicReference<>();
    private final @NonNull AtomicReference<MissingIdStrategy> missingIdStrategy =
        new AtomicReference<>();

    public @NonNull FeatureFlags getFeatureFlags() {
      return this.flags.get();
    }

    public @NonNull ParseMode getParseMode() {
      return this.parseMode.get();
    }

    public @NonNull TtlScheduler getTtlScheduler() {
      return this.ttlScheduler.get();
    }

    public @NonNull MissingIdStrategy getMissingIdStrategy() {
      return this.missingIdStrategy.get();
    }

    public boolean isSanitize() {
      return this.sanitize.get();
    }

    public @NonNull DSLGlobalConfig markdownV2() {
      log.debug("[core-init] MARKDOWN_V2 enabled");
      this.parseMode.set(ParseMode.MARKDOWN_V2);
      return this;
    }

    public @NonNull DSLGlobalConfig markdown() {
      log.debug("[core-init] MARKDOWN enabled");
      this.parseMode.set(ParseMode.MARKDOWN);
      return this;
    }

    public @NonNull DSLGlobalConfig html() {
      log.debug("[core-init] HTML enabled");
      this.parseMode.set(ParseMode.HTML);
      return this;
    }

    public @NonNull DSLGlobalConfig sanitize() {
      log.debug("[core-init] sanitize enabled");
      this.sanitize.set(true);
      return this;
    }

    public @NonNull DSLGlobalConfig noSanitize() {
      log.debug("[core-init] sanitize disabled");
      this.sanitize.set(false);
      return this;
    }

    public @NonNull DSLGlobalConfig featureFlags(@NonNull FeatureFlags flags) {
      log.debug("[core-init] FeatureFlags changed to {}", flags.getClass().getSimpleName());
      this.flags.set(flags);
      return this;
    }

    public @NonNull DSLGlobalConfig missingIdStrategy(@NonNull MissingIdStrategy strategy) {
      log.debug("[core-init] MissingIdStrategy changed to {}", strategy.getClass().getSimpleName());
      this.missingIdStrategy.set(strategy);
      return this;
    }

    public @NonNull DSLGlobalConfig ttlScheduler(@NonNull TtlScheduler ttlScheduler) {
      log.debug("[core-init] TtlScheduler changed to {}", ttlScheduler.getClass().getSimpleName());
      this.ttlScheduler.set(ttlScheduler);
      return this;
    }

    void close() {
      try {
        ttlScheduler.get().close();
      } catch (Exception ignored) {
      }
    }
  }

  public static class WebhookGlobalConfig {
    private final AtomicReference<WebhookServer> server = new AtomicReference<>();
    private final AtomicReference<WebhookServer.Engine> engine =
        new AtomicReference<>(WebhookServer.Engine.JETTY);
    private final AtomicReference<String> secret = new AtomicReference<>();
    private final AtomicReference<Integer> port = new AtomicReference<>(0);

    public @NonNull WebhookGlobalConfig engine(@NonNull WebhookServer.Engine engine) {
      this.engine.set(engine);
      return this;
    }

    public @NonNull WebhookGlobalConfig secret(@NonNull String secret) {
      this.secret.set(secret);
      return this;
    }

    public @NonNull WebhookGlobalConfig port(int port) {
      this.port.set(port);
      return this;
    }

    public int port() {
      return this.port.get();
    }

    public String secret() {
      return this.secret.get();
    }

    public void start() {
      WebhookServer srv = new WebhookServer(port.get(), secret.get(), engine.get());
      try {
        srv.start();
      } catch (Exception e) {
        throw new IllegalStateException("Webhook server start failed", e);
      }
      server.set(srv);
      port.set(srv.port());
    }

    public WebhookServer server() {
      return server.get();
    }

    void close() {
      WebhookServer srv = server.get();
      if (srv != null) {
        try {
          srv.close();
        } catch (Exception ignored) {
        }
      }
    }
  }

  public static class ObservabilityGlobalConfig {
    private final AtomicReference<MetricsCollector> collector =
        new AtomicReference<>(new NoOpMetricsCollector());

    public @NonNull MetricsCollector getCollector() {
      return collector.get();
    }

    public BotGlobalConfig.@NonNull ObservabilityGlobalConfig collector(
        @NonNull MetricsCollector metricsCollector) {
      log.debug(
          "[core-init] MetricsCollector changed to {}",
          metricsCollector.getClass().getSimpleName());
      collector.set(metricsCollector);
      return this;
    }

    void close() {
      try {
        collector.get().close();
      } catch (Exception ignored) {
      }
    }
  }
}
