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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class CustomStrategyReuseTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void sameLambdaUsedTwice() throws TelegramApiException {
    List<String> log = new ArrayList<>();
    MissingIdStrategy audit = (name, c) -> log.add("missing " + name);

    TelegramSender sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<PartialBotApiMethod<?>>any());

    DSLContext ctx = MockCtx.ctx(/*chat*/ (Long) null, 9L, sender);

    MessageBuilder b1 = new MessageBuilder(ctx, "1").missingIdStrategy(audit);
    b1.requireChatId();

    MessageBuilder b2 = new MessageBuilder(ctx, "2").missingIdStrategy(audit);
    b2.requireChatId();

    assertThat(log).containsExactly("missing chatId", "missing chatId");
  }
}
