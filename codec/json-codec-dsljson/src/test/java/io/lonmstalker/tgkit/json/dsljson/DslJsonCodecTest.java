package io.lonmstalker.tgkit.json.dsljson;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.json.JsonCodec;
import java.util.Locale;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;

/** Тест базового round-trip сериализации. */
class DslJsonCodecTest {

  private final JsonCodec codec =
      ServiceLoader.load(JsonCodec.class).findFirst().orElseThrow();

  @Test
  void roundTrip() throws Exception {
    BotConfig cfg =
        BotConfig.builder().botGroup("demo").requestsPerSecond(42).locale(Locale.FRANCE).build();

    byte[] json = codec.toBytes(cfg);
    BotConfig actual = codec.fromBytes(json, BotConfig.class);

    assertThat(actual.getBotGroup()).isEqualTo(cfg.getBotGroup());
    assertThat(actual.getRequestsPerSecond()).isEqualTo(cfg.getRequestsPerSecond());
    assertThat(actual.getLocale()).isEqualTo(cfg.getLocale());
  }
}
