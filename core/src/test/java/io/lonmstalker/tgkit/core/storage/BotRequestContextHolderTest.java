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
package io.lonmstalker.tgkit.core.storage;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotRequestContextHolderTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void setAndGet() {
    Update u = new Update();
    TelegramSender sender = new TelegramSender(BotConfig.builder().build(), "token") {};
    BotRequestContextHolder.setUpdate(u);
    BotRequestContextHolder.setSender(sender);

    assertEquals(u, BotRequestContextHolder.getUpdate());
    assertEquals(sender, BotRequestContextHolder.getSender());
    assertEquals(u, BotRequestContextHolder.getUpdateNotNull());
    assertEquals(sender, BotRequestContextHolder.getSenderNotNull());
  }

  @Test
  void clearAndExceptions() {
    BotRequestContextHolder.clear();
    assertNull(BotRequestContextHolder.getUpdate());
    assertNull(BotRequestContextHolder.getSender());
    assertThrows(RuntimeException.class, BotRequestContextHolder::getUpdateNotNull);
    assertThrows(RuntimeException.class, BotRequestContextHolder::getSenderNotNull);
  }
}
