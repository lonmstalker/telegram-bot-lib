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
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

class MessageBuilderTest {

  private DSLContext ctx;

  static {
    BotCoreInitializer.init();
  }

  @BeforeEach
  void init() {
    BotInfo bot = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(555L);
    when(user.userId()).thenReturn(999L);
    ctx = new DSLContext.SimpleDSLContext(mock(BotService.class), bot, user);
  }

  @Test
  void buildCreatesSanitizedMessage() {
    BotGlobalConfig.INSTANCE.dsl().markdownV2().sanitize();

    SendMessage msg =
        (SendMessage) new MessageBuilder(ctx, "*bold*").parseMode(ParseMode.MARKDOWN_V2).build();

    assertThat(msg.getChatId()).isEqualTo("555");
    assertThat(msg.getParseMode()).isEqualTo("MarkdownV2");
    assertThat(msg.getText()).isEqualTo("\\*bold\\*"); // Markdown-экранировано
  }
}
