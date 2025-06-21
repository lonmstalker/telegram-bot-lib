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
package io.github.tgkit.core.config;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.testkit.TestBotBootstrap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BotConfigLoaderTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @TempDir Path tmp;

  @Test
  void loadYaml() throws Exception {
    Path f = tmp.resolve("cfg.yaml");
    Files.writeString(
        f,
        "token: T\n"
            + "base-url: http://localhost\n"
            + "bot-group: g\n"
            + "requests-per-second: 20\n"
            + "packages:\n  - io.test\n");

    BotConfigLoader.Settings cfg = BotConfigLoader.load(f);
    assertEquals("T", cfg.token());
    assertEquals("http://localhost", cfg.baseUrl());
    assertEquals("g", cfg.botGroup());
    assertEquals(20, cfg.requestsPerSecond());
    assertEquals(List.of("io.test"), cfg.packages());
  }

  @Test
  void loadJson() throws Exception {
    Path f = tmp.resolve("cfg.json");
    String json =
        "{"
            + "\"token\":\"T\","
            + "\"base-url\":\"http://localhost\","
            + "\"bot-group\":\"g\","
            + "\"requests-per-second\":20,"
            + "\"packages\":[\"io.test\"]"
            + "}";
    Files.writeString(f, json);

    BotConfigLoader.Settings cfg = BotConfigLoader.load(f);
    assertEquals("T", cfg.token());
    assertEquals("http://localhost", cfg.baseUrl());
    assertEquals("g", cfg.botGroup());
    assertEquals(20, cfg.requestsPerSecond());
    assertEquals(List.of("io.test"), cfg.packages());
  }
}
