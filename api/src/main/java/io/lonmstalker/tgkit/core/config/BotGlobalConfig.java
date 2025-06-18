package io.lonmstalker.tgkit.core.config;

import io.lonmstalker.tgkit.core.dsl.MissingIdStrategy;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.lonmstalker.tgkit.core.dsl.ttl.TtlSchedulerDefault;
import io.lonmstalker.tgkit.core.event.BotEventBus;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Настройки по умолчанию для BotResponse.
 */
@Slf4j
public class BotGlobalConfig {

    /**
     * Глобальная конфигурация.
     */
    public static final BotGlobalConfig INSTANCE = new BotGlobalConfig();
    private final @NonNull DSLGlobalConfig dslGlobalConfig;
    private final @NonNull HttpGlobalConfig httpGlobalConfig;
    private final @NonNull EventGlobalConfig eventGlobalConfig;
    private final @NonNull ExecutorsGlobalConfig executorsGlobalConfig;

    private BotGlobalConfig() {
        this.executorsGlobalConfig = new ExecutorsGlobalConfig();

        this.dslGlobalConfig = new DSLGlobalConfig();
        this.httpGlobalConfig = new HttpGlobalConfig();
        this.eventGlobalConfig = new EventGlobalConfig();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            eventGlobalConfig.close();
            httpGlobalConfig.close();
            dslGlobalConfig.close();
            executorsGlobalConfig.close();
        }));
    }

    public @NonNull EventGlobalConfig eventBus() {
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

    @Getter
    public static class EventGlobalConfig {
        private final @NonNull AtomicReference<BotEventBus> eventBus = new AtomicReference<>();

        public @NonNull BotEventBus getBus() {
            return this.eventBus.get();
        }

        public @NonNull EventGlobalConfig bus(@NonNull BotEventBus eventBus) {
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

    @Getter
    public static class HttpGlobalConfig {
        private final @NonNull AtomicReference<@NonNull HttpClient> client =
                new AtomicReference<>(HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(75)).build());

        public @NonNull HttpGlobalConfig httpClient(@NonNull HttpClient httpClient) {
            this.client.set(httpClient);
            return this;
        }

        public @NonNull HttpClient getClient() {
            return this.client.get();
        }

        void close() {
            client.get().close();
        }
    }

    @Getter
    public static class ExecutorsGlobalConfig {
        private final @NonNull AtomicReference<@NonNull ScheduledExecutorService> scheduledExecutorService =
                new AtomicReference<>(Executors.newScheduledThreadPool(1));
        private final @NonNull AtomicReference<@NonNull ExecutorService> ioExecutorService =
                new AtomicReference<>(Executors.newVirtualThreadPerTaskExecutor());

        public BotGlobalConfig.@NonNull ExecutorsGlobalConfig scheduledExecutorService(
                @NonNull ScheduledExecutorService scheduledExecutorService) {
            this.scheduledExecutorService.set(scheduledExecutorService);
            return this;
        }

        public BotGlobalConfig.@NonNull ExecutorsGlobalConfig ioExecutorService(@NonNull ExecutorService ioExecutor) {
            this.ioExecutorService.set(ioExecutor);
            return this;
        }

        public @NonNull ScheduledExecutorService getScheduledExecutorService() {
            return this.scheduledExecutorService.get();
        }

        public @NonNull ExecutorService getIoExecutorService() {
            return this.ioExecutorService.get();
        }

        void close() {
            ioExecutorService.get().shutdown();
            scheduledExecutorService.get().shutdownNow();
        }
    }

    @Getter
    public static class DSLGlobalConfig {
        private volatile boolean sanitize;
        private volatile @NonNull ParseMode parseMode = ParseMode.HTML;
        private volatile @NonNull FeatureFlags flags = new InMemoryFeatureFlags();
        private volatile @NonNull TtlScheduler ttlScheduler = new TtlSchedulerDefault();
        private volatile @NonNull MissingIdStrategy missingIdStrategy = MissingIdStrategy.ERROR;

        public @NonNull DSLGlobalConfig markdownV2() {
            this.parseMode = ParseMode.MARKDOWN_V2;
            return this;
        }

        public @NonNull DSLGlobalConfig sanitizeMarkdown() {
            this.sanitize = true;
            return this;
        }

        public @NonNull DSLGlobalConfig unSanitizeMarkdown() {
            this.sanitize = false;
            return this;
        }

        public @NonNull DSLGlobalConfig featureFlags(@NonNull FeatureFlags flags) {
            this.flags = flags;
            return this;
        }

        public @NonNull DSLGlobalConfig missingIdStrategy(@NonNull MissingIdStrategy strategy) {
            this.missingIdStrategy = strategy;
            return this;
        }

        public @NonNull DSLGlobalConfig ttlScheduler(@NonNull TtlScheduler ttlScheduler) {
            this.ttlScheduler = ttlScheduler;
            return this;
        }

        void close() {
            try {
                ttlScheduler.close();
            } catch (Exception ignored) {
            }
        }
    }
}
