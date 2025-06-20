package io.lonmstalker.tgkit.json.dsljson;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.json.JsonCodec;
import org.junit.jupiter.api.Test;

/** Проверяет базовый round-trip сериализации. */
class DslJsonCodecTest {

  @Test
  void encodeDecodeBotConfig() {
    JsonCodec codec = new DslJsonCodec();
    BotConfig cfg = BotConfig.builder().botGroup("g").requestsPerSecond(42).build();

    byte[] json = codec.toBytes(cfg);
    BotConfig result = codec.fromBytes(json, BotConfig.class);

    assertThat(result.getBotGroup()).isEqualTo(cfg.getBotGroup());
    assertThat(result.getRequestsPerSecond()).isEqualTo(cfg.getRequestsPerSecond());
  }
}
