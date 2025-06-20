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
package io.lonmstalker.tgkit.core.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.TelegramBot;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.testkit.TelegramMockServer;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TelegramBotRunTest {

  @TempDir Path tmp;

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void startFromYaml() throws Exception {
    try (TelegramMockServer server = new TelegramMockServer()) {
      Path f = tmp.resolve("bot.yaml");
      String yaml =
          "token: T\n"
              + "base-url: "
              + server.baseUrl()
              + "\n"
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
