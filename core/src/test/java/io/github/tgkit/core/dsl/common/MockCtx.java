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
package io.github.tgkit.internal.dsl.common;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.tgkit.internal.BotInfo;
import io.github.tgkit.internal.BotService;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.dsl.context.DSLContext;
import io.github.tgkit.internal.i18n.MessageLocalizer;
import io.github.tgkit.internal.user.BotUserInfo;
import java.util.Set;

public class MockCtx {

  // фиктивные BotInfo / BotUserInfo
  public static DSLContext ctx(Long chatId, long userId, TelegramSender sender) {
    BotService bot = mock(BotService.class);
    when(bot.sender()).thenReturn(sender);
    when(bot.localizer()).thenReturn(mock(MessageLocalizer.class));

    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(chatId);
    when(user.userId()).thenReturn(userId);
    when(user.roles()).thenReturn(Set.of("ADMIN"));

    return new DSLContext.SimpleDSLContext(bot, mock(BotInfo.class), user);
  }
}
