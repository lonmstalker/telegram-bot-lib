package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.core.bot.BotRegistry;
import io.lonmstalker.tgkit.core.bot.BotRegistryImpl;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.event.BotEventBus;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import java.net.http.HttpClient;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutorService;
import org.checkerframework.checker.nullness.qual.NonNull;

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
