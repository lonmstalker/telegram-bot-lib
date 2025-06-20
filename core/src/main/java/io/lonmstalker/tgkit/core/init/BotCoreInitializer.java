/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.init;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.MissingIdStrategy;
import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.lonmstalker.tgkit.core.dsl.ttl.TtlSchedulerDefault;
import io.lonmstalker.tgkit.core.event.InMemoryEventBus;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Базовая инициализация ядра. <br>
 * Вызывается ровно один раз на старте приложения — например, в вашем {@code main()} или
 * {@code @SpringBootApplication}.
 *
 * <p>👉 Не обязательно использовать этот класс — конфиг можно править вручную. Но он наглядно
 * демонстрирует, как корректно обновлять глобальное состояние.
 */
public final class BotCoreInitializer {

  private static final Logger log = LoggerFactory.getLogger(BotCoreInitializer.class);

  private BotCoreInitializer() {}

  private static volatile boolean started;

  public synchronized void init() {
    if (started) {
      log.warn(
          "[core-init] BotCoreInitializer уже вызывался, повторная инициализация игнорируется");
      return;
    }
    log.info("[core-init] Старт инициализации ядра…");

    // ── DSL / Markdown ────────────────────────────────────────────────────
    BotGlobalConfig.INSTANCE
        .dsl()
        .markdownV2() // глобальный MarkdownV2
        .sanitize() // экранировать спец. символы
        .featureFlags(new InMemoryFeatureFlags()) // in-mem FF (можно заменить)
        .ttlScheduler(new TtlSchedulerDefault()) // дефолтный планировщик TTL
        .missingIdStrategy(MissingIdStrategy.ERROR);

    // ── HTTP-клиент с собственным таймаутом ───────────────────────────────
    BotGlobalConfig.INSTANCE
        .http()
        .httpClient(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(30)).build());

    // ── Executors ────────────────────────────────────────────────────────
    BotGlobalConfig.INSTANCE
        .executors()
        .cpuExecutorService(Executors.newWorkStealingPool(2))
        .ioExecutorService(Executors.newVirtualThreadPerTaskExecutor())
        .scheduledExecutorService(Executors.newScheduledThreadPool(2));

    // ── Events ────────────────────────────────────────────────────────
    BotGlobalConfig.INSTANCE.events().bus(new InMemoryEventBus());

    log.info("[core-init] Ядро TGKIT успешно инициализировано ✅");
    started = true;
  }
}
