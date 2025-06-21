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
package io.github.tgkit.core.bot;

import io.github.tgkit.core.BotAdapter;
import io.github.tgkit.core.bot.BotDataSourceFactory.BotData;
import io.github.tgkit.core.bot.loader.AnnotatedCommandLoader;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.crypto.TokenCipher;
import java.util.concurrent.atomic.AtomicLong;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

/**
 * Фабрика создания {@link Bot} из разных источников данных.
 *
 * <p>Типичный способ инициализации:
 *
 * <pre>{@code
 * Bot bot = BotFactory.INSTANCE.from(
 *         token,
 *         BotConfig.builder().build(),
 *         update -> null,
 *         "com.example.bot");
 * bot.start();
 * }</pre>
 */
public final class BotFactory {
  private BotFactory() {}

  public static final BotFactory INSTANCE = new BotFactory();
  private final AtomicLong nextId = new AtomicLong();

  /**
   * Создаёт бота с long polling.
   *
   * @param token токен BotFather
   * @param config конфигурация бота
   * @param adapter адаптер для обработки обновлений
   * @return запущенный бот
   */
  public @NonNull Bot from(
      @NonNull String token, @NonNull BotConfig config, @NonNull BotAdapter adapter) {
    TelegramSender sender = new TelegramSender(config, token);
    var bot =
        implBuilder(token, config)
            .session(new BotSessionImpl(null, null, config.getUpdateQueueCapacity()))
            .absSender(
                new LongPollingReceiver(
                    config, adapter, token, sender, config.getGlobalExceptionHandler()))
            .build();
    if (adapter instanceof BotAdapterImpl b) {
      b.setCurrentBot(bot);
    }
    return bot;
  }

  /** Создаёт бота и сканирует указанные пакеты на предмет команд. */
  public @NonNull Bot from(
      @NonNull String token,
      @NonNull BotConfig config,
      @NonNull BotAdapter adapter,
      @NonNull String... packages) {
    Bot bot = from(token, config, adapter);
    AnnotatedCommandLoader.load(bot.registry(), packages);
    return bot;
  }

  /** Создаёт бота в режиме webhook. */
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

  /** Webhook-биржа с автоподключением команд из пакетов. */
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

  /** Загружает конфигурацию бота из внешнего источника. */
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

  /** Загружает конфигурацию из источника и сканирует пакеты. */
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
