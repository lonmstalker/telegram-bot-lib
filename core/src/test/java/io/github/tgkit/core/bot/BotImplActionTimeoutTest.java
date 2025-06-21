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
package io.github.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.testkit.TestBotBootstrap;
import java.io.Serializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;

class BotImplActionTimeoutTest {

  static {
    TestBotBootstrap.initOnce();
  }

  private DummySender sender;

  @BeforeEach
  void setUp() {
    sender = new DummySender(new DefaultBotOptions());
  }

  @Test
  void defaultTimeoutFromConstant() {
    BotConfig cfg = BotConfig.builder().build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .build();
    assertEquals(
        BotConfig.DEFAULT_ON_COMPLETED_ACTION_TIMEOUT_MS, bot.getOnCompletedActionTimeoutMs());
  }

  @Test
  void timeoutFromConfig() {
    BotConfig cfg = BotConfig.builder().onCompletedActionTimeoutMs(12345L).build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .build();
    assertEquals(12345L, bot.getOnCompletedActionTimeoutMs());
  }

  @Test
  void builderOverridesConfig() {
    BotConfig cfg = BotConfig.builder().onCompletedActionTimeoutMs(5000L).build();
    BotImpl bot =
        BotImpl.builder()
            .id(1L)
            .token("T")
            .config(cfg)
            .absSender(sender)
            .commandRegistry(new BotCommandRegistryImpl())
            .onCompletedActionTimeoutMs(20000L)
            .build();
    assertEquals(20000L, bot.getOnCompletedActionTimeoutMs());
  }

  private static final class DummySender extends DefaultAbsSender {
    DummySender(DefaultBotOptions opt) {
      super(opt, "TOKEN");
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
      if (method instanceof GetMe) {
        User u = new User();
        u.setId(42L);
        u.setUserName("tester");
        return (T) u;
      }
      return null;
    }

    @Override
    public void close() {}
  }
}
