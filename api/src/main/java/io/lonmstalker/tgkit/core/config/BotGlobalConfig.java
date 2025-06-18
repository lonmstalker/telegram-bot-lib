package io.lonmstalker.tgkit.core.config;

import io.lonmstalker.tgkit.core.dsl.MissingIdStrategy;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.event.BotEventBus;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.http.HttpClient;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
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

        public @NonNull HttpGlobalConfig httpClient(@NonNull HttpClient httpClient) {
            log.debug("[core-init] HttpClient changed to {}", httpClient.getClass().getSimpleName());
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

    public static class ExecutorsGlobalConfig {
        private final @NonNull AtomicReference<ScheduledExecutorService> scheduledExecutorService =
                new AtomicReference<>();
        private final @NonNull AtomicReference<ExecutorService> ioExecutorService =
                new AtomicReference<>();

        public BotGlobalConfig.@NonNull ExecutorsGlobalConfig scheduledExecutorService(
                @NonNull ScheduledExecutorService scheduledExecutorService) {
            log.debug("[core-init] ScheduledExecutorService changed to {}",
                    scheduledExecutorService.getClass().getSimpleName());
            this.scheduledExecutorService.set(scheduledExecutorService);
            return this;
        }

        public BotGlobalConfig.@NonNull ExecutorsGlobalConfig ioExecutorService(
                @NonNull ExecutorService ioExecutor) {
            log.debug("[core-init] ExecutorService changed to {}", ioExecutor.getClass().getSimpleName());
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

    public static class DSLGlobalConfig {
        private final AtomicBoolean sanitize = new AtomicBoolean();
        private final @NonNull AtomicReference<FeatureFlags> flags = new AtomicReference<>();
        private final @NonNull AtomicReference<ParseMode> parseMode = new AtomicReference<>();
        private final @NonNull AtomicReference<TtlScheduler> ttlScheduler = new AtomicReference<>();
        private final @NonNull AtomicReference<MissingIdStrategy> missingIdStrategy = new AtomicReference<>();

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
}
