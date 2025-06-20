package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.BotDataSourceFactory.BotData;
import io.lonmstalker.tgkit.core.bot.loader.AnnotatedCommandLoader;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.crypto.TokenCipher;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

public final class BotFactory {
  private BotFactory() {}

  public static final BotFactory INSTANCE = new BotFactory();
  private final AtomicLong nextId = new AtomicLong();

  public @NonNull Bot from(
      @NonNull String token, @NonNull BotConfig config, @NonNull BotAdapter adapter) {
    TelegramSender sender = new TelegramSender(config, token);
    var bot =
        implBuilder(token, config)
            .session(new BotSessionImpl())
            .absSender(
                new LongPollingReceiver(
                    config, adapter, token, sender, config.getGlobalExceptionHandler()))
            .build();
    if (adapter instanceof BotAdapterImpl b) {
      b.setCurrentBot(bot);
    }
    return bot;
  }

  public @NonNull Bot from(
      @NonNull String token,
      @NonNull BotConfig config,
      @NonNull BotAdapter adapter,
      @NonNull String... packages) {
    Bot bot = from(token, config, adapter);
    AnnotatedCommandLoader.load(bot.registry(), packages);
    return bot;
  }

  public @NonNull Bot from(
      @NonNull String token,
      @NonNull BotConfig config,
      @NonNull BotAdapter adapter,
      @NonNull SetWebhook setWebhook) {
    TelegramSender sender = new TelegramSender(config, token);
    WebHookReceiver receiver =
        new WebHookReceiver(config, adapter, token, sender, config.getGlobalExceptionHandler());
    BotGlobalConfig.INSTANCE.webhook().server().register(receiver);
    setWebhook.setUrl(
        "http://localhost:" + BotGlobalConfig.INSTANCE.webhook().port() + "/" + token);
    setWebhook.setSecretToken(BotGlobalConfig.INSTANCE.webhook().secret());
    var bot = implBuilder(token, config).setWebhook(setWebhook).absSender(receiver).build();
    if (adapter instanceof BotAdapterImpl b) {
      b.setCurrentBot(bot);
    }
    return bot;
  }

  public @NonNull Bot from(
      @NonNull String token,
      @NonNull BotConfig config,
      @NonNull BotAdapter adapter,
      @NonNull SetWebhook setWebhook,
      @NonNull String... packages) {
    Bot bot = from(token, config, adapter, setWebhook);
    AnnotatedCommandLoader.load(bot.registry(), packages);
    return bot;
  }

  public @NonNull Bot from(
      long botId,
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
      cfg.setBotGroup(data.config().getBotGroup());
      cfg.setGetUpdatesTimeout(data.config().getGetUpdatesTimeout());
      cfg.setGetUpdatesLimit(data.config().getGetUpdatesLimit());
    }
    return from(data.token(), cfg, adapter);
  }

  public @NonNull Bot from(
      long botId,
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
