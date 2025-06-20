package io.lonmstalker.tgkit.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.TelegramBot;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.testkit.TelegramMockServer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TelegramBotRunTest {

  @TempDir Path tmp;

  static {
    BotCoreInitializer.init();
  }

  @Test
  void startFromYaml() throws Exception {
    try (TelegramMockServer server = new TelegramMockServer()) {
      Path f = tmp.resolve("bot.yaml");
      String yaml =
          "token: T\n"
              + "base-url: " + server.baseUrl() + "\n"
              + "bot-group: g\n"
              + "requests-per-second: 20\n"
              + "packages:\n  - io.test\n";
      Files.writeString(f, yaml);

      Bot bot = TelegramBot.run(f);
      assertThat(server.takeRequest(1, TimeUnit.SECONDS).path()).endsWith("/getMe");
      bot.stop();
    }
  }
}
