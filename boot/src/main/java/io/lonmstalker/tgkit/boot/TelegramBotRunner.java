package io.lonmstalker.tgkit.boot;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotAdapter;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * Компонент для запуска Telegram-бота при старте Spring Boot приложения.
 */
public final class TelegramBotRunner implements ApplicationRunner {

  private final BotProperties props;
  private @Nullable Bot bot;

  public TelegramBotRunner(BotProperties props) {
    this.props = props;
  }

  @Override
  public void run(ApplicationArguments args) throws Exception {
    BotCoreInitializer.init();

    BotConfig cfg =
        BotConfig.builder()
            .baseUrl(props.getBaseUrl())
            .botGroup(props.getBotGroup())
            .requestsPerSecond(props.getRequestsPerSecond())
            .build();

    TelegramSender sender = new TelegramSender(cfg, props.getToken());
    BotAdapter adapter = BotAdapterImpl.builder().sender(sender).config(cfg).build();
    List<String> pkgs = props.getPackages();
    bot = BotFactory.INSTANCE.from(props.getToken(), cfg, adapter, pkgs.toArray(new String[0]));
    bot.start();
  }

  /** Останавливает запущенного бота. */
  public void stop() {
    if (bot != null) {
      bot.stop();
    }
  }

  /** Возвращает запущенный экземпляр бота или {@code null}. */
  public @Nullable Bot bot() {
    return bot;
  }
}
