package io.lonmstalker.tgkit.core.dsl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class EditBuilderTypingTest {

  static {
    BotCoreInitializer.init();
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
