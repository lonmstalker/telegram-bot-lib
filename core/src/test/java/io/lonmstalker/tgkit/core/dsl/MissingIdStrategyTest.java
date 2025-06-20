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
package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import org.junit.jupiter.api.Test;

class MissingIdStrategyTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void errorStrategyThrows() {
    DSLContext ctx = ctxWithNullChat();
    MessageBuilder b = new MessageBuilder(ctx, "hi").missingIdStrategy(MissingIdStrategy.ERROR);

    assertThatThrownBy(b::requireChatId)
        .isInstanceOfAny(RuntimeException.class, BotApiException.class);
  }

  @Test
  void warnStrategyDoesNotThrow() {
    DSLContext ctx = ctxWithNullChat();
    MessageBuilder b = new MessageBuilder(ctx, "hi").missingIdStrategy(MissingIdStrategy.WARN);

    assertThatCode(b::requireChatId).doesNotThrowAnyException();
  }

  @Test
  void ignoreStrategySilent() {
    DSLContext ctx = ctxWithNullChat();
    MessageBuilder b = new MessageBuilder(ctx, "hi").missingIdStrategy(MissingIdStrategy.IGNORE);

    assertThatCode(b::requireChatId).doesNotThrowAnyException();
  }

  private DSLContext ctxWithNullChat() {
    BotInfo botInfo = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(null);
    when(user.userId()).thenReturn(42L);
    return new DSLContext.SimpleDSLContext(mock(BotService.class), botInfo, user);
  }
}
