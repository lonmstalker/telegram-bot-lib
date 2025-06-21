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

import io.github.tgkit.api.bot.BotRegistry;
import io.github.tgkit.api.config.BotGlobalConfig;
import io.github.tgkit.api.dsl.feature_flags.FeatureFlags;
import io.github.tgkit.api.event.BotEventBus;
import io.github.tgkit.api.ttl.TtlScheduler;
import io.github.tgkit.api.bot.BotRegistryImpl;
import io.github.tgkit.security.audit.AuditBus;
import io.github.tgkit.security.config.BotSecurityGlobalConfig;
import io.github.tgkit.security.secret.SecretStore;
import java.net.http.HttpClient;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Стандартная реализация {@link BotPluginContext}, предоставляющая сервисы через {@link
 * ServiceLoader}.
 */
public class BotPluginContextDefault implements BotPluginContext {
  private final ClassLoader cl;

  BotPluginContextDefault(@NonNull ClassLoader cl) {
    this.cl = cl;
  }

  @Override
  public <T> T getService(@NonNull Class<T> type) {
    // naive DI: ServiceLoader within plugin CL
    return ServiceLoader.load(type, cl)
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No service " + type));
  }

  @Override
  public @NonNull BotGlobalConfig config() {
    return BotGlobalConfig.INSTANCE;
  }

  @Override
  public @NonNull TtlScheduler scheduler() {
    return BotGlobalConfig.INSTANCE.dsl().getTtlScheduler();
  }

  @Override
  public @NonNull FeatureFlags featureFlags() {
    return BotGlobalConfig.INSTANCE.dsl().getFeatureFlags();
  }

  @Override
  public @NonNull AuditBus audit() {
    return BotSecurityGlobalConfig.INSTANCE.audit().bus();
  }

  @Override
  public @NonNull SecretStore secrets() {
    return BotSecurityGlobalConfig.INSTANCE.secrets().getStore();
  }

  @Override
  public @NonNull BotEventBus eventBus() {
    return BotGlobalConfig.INSTANCE.events().getBus();
  }

  @Override
  public @NonNull BotRegistry registry() {
    return BotRegistryImpl.getInstance();
  }

  @Override
  public @NonNull ExecutorService ioExecutor() {
    return BotGlobalConfig.INSTANCE.executors().getIoExecutorService();
  }

  @Override
  public @NonNull HttpClient http() {
    return BotGlobalConfig.INSTANCE.http().getClient();
  }
}
