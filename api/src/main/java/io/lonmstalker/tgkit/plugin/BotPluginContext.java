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

package io.github.tgkit.plugin;

import io.github.tgkit.core.bot.BotRegistry;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.github.tgkit.core.event.BotEventBus;
import io.github.tgkit.core.ttl.TtlScheduler;
import io.github.tgkit.security.audit.AuditBus;
import io.github.tgkit.security.secret.SecretStore;
import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface BotPluginContext {

  /**
   * Получить spi сервис
   */
  @Nullable
  <T> T getService(@NonNull Class<T> type);

  /**
   * Основной конфиг (на чтение).
   */
  @NonNull
  BotGlobalConfig config();

  /**
   * Планировщик TTL-/cron-задач.
   */
  @NonNull
  TtlScheduler scheduler();

  /**
   * Feature-flags (LaunchDarkly / Redis).
   */
  @NonNull
  FeatureFlags featureFlags();

  /**
   * Централизованный Audit-шлюз.
   */
  @NonNull
  AuditBus audit();

  /**
   * Храним и забираем секреты.
   */
  @NonNull
  SecretStore secrets();

  /**
   * Рассылка/подписка на события ядра.
   */
  @NonNull
  BotEventBus eventBus();

  @NonNull
  BotRegistry registry();

  /**
   * Выделенный CPU-/I/O-executor плагина.
   */
  @NonNull
  ExecutorService ioExecutor();

  /**
   * Готовый настроенный HTTP-клиент (TLS, proxy, retry).
   */
  @NonNull
  HttpClient http();
}
