package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.TelegramBot;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.nio.file.Path;

public class SimpleBotApplication {

  public static void main(String[] args) {
    BotCoreInitializer.init();
    TelegramBot.run(Path.of("bot.yaml"));
  }
}
