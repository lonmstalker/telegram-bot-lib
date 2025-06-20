package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
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

  TelegramSender sender;
  DSLContext ctx;

  static {
    BotCoreInitializer.init();
  }

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
