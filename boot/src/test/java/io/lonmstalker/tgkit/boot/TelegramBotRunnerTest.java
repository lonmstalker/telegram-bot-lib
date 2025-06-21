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
package io.lonmstalker.tgkit.boot;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.github.tgkit.testkit.TelegramMockServer;
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
