package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.BotDataSourceFactory.BotData;
import io.lonmstalker.tgkit.core.crypto.TokenCipher;
import io.lonmstalker.tgkit.core.loader.AnnotatedCommandLoader;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import java.util.concurrent.atomic.AtomicLong;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotFactory {
    public static final BotFactory INSTANCE = new BotFactory();
    private final AtomicLong nextId = new AtomicLong();

    public @NonNull Bot from(@NonNull String token,
                             @NonNull BotConfig config,
                             @NonNull BotAdapter adapter) {
        return implBuilder(token, config)
                .absSender(new LongPollingReceiver(config, adapter, token, config.getGlobalExceptionHandler()))
                .session(new BotSessionImpl())
                .build();
    }

    public @NonNull Bot from(@NonNull String token,
                             @NonNull BotConfig config,
                             @NonNull BotAdapter adapter,
                             @NonNull String... packages) {
        Bot bot = from(token, config, adapter);
        AnnotatedCommandLoader.load(bot.registry(), packages);
        return bot;
    }

    public @NonNull Bot from(@NonNull String token,
                             @NonNull BotConfig config,
                             @NonNull BotAdapter adapter,
                             @NonNull SetWebhook setWebhook) {
        return implBuilder(token, config)
                .setWebhook(setWebhook)
                .absSender(new WebHookReceiver(config, adapter, token, config.getGlobalExceptionHandler()))
                .build();
    }

    public @NonNull Bot from(@NonNull String token,
                             @NonNull BotConfig config,
                             @NonNull BotAdapter adapter,
                             @NonNull SetWebhook setWebhook,
                             @NonNull String... packages) {
        Bot bot = from(token, config, adapter, setWebhook);
        AnnotatedCommandLoader.load(bot.registry(), packages);
        return bot;
    }

    public @NonNull Bot from(long botId,
                             @NonNull BotDataSourceConfig config,
                             @NonNull BotAdapter adapter,
                             @NonNull TokenCipher cipher) {
        BotData data = BotDataSourceFactory.INSTANCE.load(config.getDataSource(), botId, cipher);
        BotConfig cfg = config.getBotConfig();
        if (cfg == null) {
            cfg = data.config();
        } else {
            cfg.setProxyHost(data.config().getProxyHost());
            cfg.setProxyPort(data.config().getProxyPort());
            cfg.setProxyType(data.config().getProxyType());
            cfg.setMaxThreads(data.config().getMaxThreads());
            cfg.setBotPattern(data.config().getBotPattern());
            cfg.setGetUpdatesTimeout(data.config().getGetUpdatesTimeout());
            cfg.setGetUpdatesLimit(data.config().getGetUpdatesLimit());
        }
        return from(data.token(), cfg, adapter);
    }

    public @NonNull Bot from(long botId,
                             @NonNull BotDataSourceConfig config,
                             @NonNull BotAdapter adapter,
                             @NonNull TokenCipher cipher,
                             @NonNull String... packages) {
        Bot bot = from(botId, config, adapter, cipher);
        AnnotatedCommandLoader.load(bot.registry(), packages);
        return bot;
    }

    private long nextId() {
        return nextId.incrementAndGet();
    }

    private BotImpl.BotImplBuilder implBuilder(@NonNull String token, @NonNull BotConfig config) {
        return BotImpl.builder()
                .id(nextId())
                .token(token)
                .config(config)
                .commandRegistry(new BotCommandRegistryImpl());
    }
}
