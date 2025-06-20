package io.lonmstalker.tgkit.core.config;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BotConfigLoaderTest {

  @TempDir Path tmp;

  static {
    BotCoreInitializer.init();
  }

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
