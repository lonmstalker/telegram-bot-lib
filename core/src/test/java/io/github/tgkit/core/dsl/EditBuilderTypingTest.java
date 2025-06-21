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
package io.github.tgkit.core.dsl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import io.github.tgkit.core.bot.TelegramSender;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.dsl.common.MockCtx;
import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class EditBuilderTypingTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void typingThenEditSanitized() throws TelegramApiException {
    var sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<SendChatAction>any());

    DSLContext ctx = MockCtx.ctx(123L, 999L, sender);

    BotGlobalConfig.INSTANCE.dsl().markdownV2().sanitize();

    new EditBuilder(ctx, 42)
        .typing(Duration.ofMillis(0)) // не ждём, но метод вызовется
        .thenEdit("*raw*")
        .build(); // build() вызывает `execute` внутри typing()

    // Проверяем, сколько раз вызван execute
    verify(sender, times(1)).execute(any(SendChatAction.class));
  }
}
