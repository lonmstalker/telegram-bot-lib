package io.lonmstalker.tgkit.boot;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.testkit.TelegramMockServer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.DefaultApplicationArguments;

class TelegramBotRunnerTest {

  @BeforeAll
  static void init() {
    BotCoreInitializer.init();
  }

  @Test
  void runAndStop() throws Exception {
    try (TelegramMockServer server = new TelegramMockServer()) {
      BotProperties props = new BotProperties();
      props.setToken("T");
      props.setBaseUrl(server.baseUrl());
      props.setPackages(List.of("io.test"));
      props.setRequestsPerSecond(1);

      TelegramBotRunner runner = new TelegramBotRunner(props);
      runner.run(new DefaultApplicationArguments(new String[0]));
      assertThat(server.takeRequest(1, TimeUnit.SECONDS).path()).endsWith("/getMe");
      Bot bot = runner.bot();
      assertThat(bot).isNotNull();
      runner.stop();
    }
  }
}
