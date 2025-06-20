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
package io.github.tgkit.internal.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import io.github.tgkit.internal.BotInfo;
import io.github.tgkit.internal.BotService;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.dsl.context.DSLContext;
import io.github.tgkit.internal.user.BotUserInfo;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.io.Serializable;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

class DslTtlIntegrationTest {

  static {
    TestBotBootstrap.initOnce();
  }

  TelegramSender sender;
  DSLContext ctx;

  @BeforeEach
  void init() {
    sender = mock(TelegramSender.class);

    /* 1️⃣  SendMessage → Message с chatId / messageId */
    when(sender.execute((PartialBotApiMethod<? extends Serializable>) any()))
        .thenAnswer(
            inv -> {
              var sm = (SendMessage) inv.getArgument(0);
              Message m = new Message();
              Chat c = new Chat();
              c.setId(Long.parseLong(sm.getChatId()));
              m.setChat(c);
              m.setMessageId(777);
              return m;
            });

    BotService service = mock(BotService.class);
    when(service.sender()).thenReturn(sender);

    BotInfo bot = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(555L);
    when(user.userId()).thenReturn(999L);

    ctx = new DSLContext.SimpleDSLContext(service, bot, user);
  }

  @Test
  void ttlSchedulesDeletion() {
    BotDSL.msg(ctx, "temp").ttl(Duration.ofMillis(200)).done().send();

    Awaitility.await()
        .atMost(2, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(sender).execute(isA(DeleteMessage.class)));

    var cap = ArgumentCaptor.forClass(DeleteMessage.class);
    verify(sender).execute(cap.capture());
    assertThat(cap.getValue().getChatId()).isEqualTo("555");
    assertThat(cap.getValue().getMessageId()).isEqualTo(777);
  }
}
