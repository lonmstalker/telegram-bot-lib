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
package io.github.tgkit.json.dsljson;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.internal.bot.BotConfig;
import io.github.tgkit.json.JsonCodec;
import java.util.Locale;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;

/** Тест базового round-trip сериализации. */
class DslJsonCodecTest {

  private final JsonCodec codec = ServiceLoader.load(JsonCodec.class).findFirst().orElseThrow();

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
