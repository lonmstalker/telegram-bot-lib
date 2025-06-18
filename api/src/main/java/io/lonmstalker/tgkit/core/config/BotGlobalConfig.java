package io.lonmstalker.tgkit.core.config;

import io.lonmstalker.tgkit.core.dsl.MissingIdStrategy;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.lonmstalker.tgkit.core.dsl.ttl.TtlSchedulerDefault;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    private final @NonNull ExecutorsGlobalConfig executorsGlobalConfig;

    private BotGlobalConfig() {
        this.dslGlobalConfig = new DSLGlobalConfig();
        this.executorsGlobalConfig = new ExecutorsGlobalConfig();
    }

    public @NonNull DSLGlobalConfig dsl() {
        return this.dslGlobalConfig;
    }

    public @NonNull ExecutorsGlobalConfig executors() {
        return this.executorsGlobalConfig;
    }

    @Getter
    public static class ExecutorsGlobalConfig {
        private final @NonNull AtomicReference<ScheduledExecutorService> scheduledExecutorService =
                new AtomicReference<>(Executors.newScheduledThreadPool(1));
        private final @NonNull AtomicReference<ExecutorService> ioExecutorService =
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
    }
}
