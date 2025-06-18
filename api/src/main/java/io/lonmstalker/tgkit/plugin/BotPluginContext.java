package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.core.bot.BotRegistry;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.event.BotEventBus;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;

public interface BotPluginContext {

    /**
     * Получить spi сервис
     */
    @Nullable
    <T> T getService(@NonNull Class<T> type);

    /** Основной конфиг (на чтение). */
    @NonNull BotGlobalConfig config();

    /** Планировщик TTL-/cron-задач. */
    @NonNull
    TtlScheduler scheduler();

    /** Feature-flags (LaunchDarkly / Redis). */
    @NonNull
    FeatureFlags featureFlags();

    /** Централизованный Audit-шлюз. */
    @NonNull AuditBus audit();

    /** Храним и забираем секреты. */
    @NonNull
    SecretStore secrets();

    /** Рассылка/подписка на события ядра. */
    @NonNull
    BotEventBus eventBus();

    @NonNull BotRegistry registry();

    /** Выделенный CPU-/I/O-executor плагина. */
    @NonNull
    ExecutorService ioExecutor();

    /** Готовый настроенный HTTP-клиент (TLS, proxy, retry). */
    @NonNull
    HttpClient http();
}
