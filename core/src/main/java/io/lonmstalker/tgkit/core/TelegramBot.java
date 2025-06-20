package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotAdapter;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.tgkit.core.config.BotConfigLoader;
import io.lonmstalker.tgkit.core.config.BotConfigLoader.Settings;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.io.IOException;
import java.nio.file.Path;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Вспомогательный класс для запуска бота по конфигурационному файлу.
 */
public final class TelegramBot {

  private TelegramBot() {}

  /**
   * Загружает конфигурацию, создаёт и запускает бота.
   *
   * @param file путь к YAML/JSON конфигурации
   * @return запущенный экземпляр {@link Bot}
   * @throws IOException если не удалось прочитать конфигурацию
   */
  public static @NonNull Bot run(@NonNull Path file) throws IOException {
    BotCoreInitializer.init();
    Settings cfg = BotConfigLoader.load(file);
    BotConfig botCfg =
        BotConfig.builder()
            .baseUrl(cfg.baseUrl())
            .botGroup(cfg.botGroup())
            .requestsPerSecond(cfg.requestsPerSecond())
            .build();
    TelegramSender sender = new TelegramSender(botCfg, cfg.token());
    BotAdapter adapter = BotAdapterImpl.builder().sender(sender).config(botCfg).build();
    String[] pkgs = cfg.packages().toArray(new String[0]);
    Bot bot = BotFactory.INSTANCE.from(cfg.token(), botCfg, adapter, pkgs);
    bot.start();
    return bot;
  }
}
