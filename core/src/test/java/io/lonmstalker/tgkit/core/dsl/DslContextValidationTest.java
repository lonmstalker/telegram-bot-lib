package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext.SimpleDSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.junit.jupiter.api.Test;

class DslContextValidationTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void shouldThrowWhenBothIdsNull() {
    BotInfo botInfo = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(null);
    when(user.userId()).thenReturn(null);

    assertThatThrownBy(() -> new SimpleDSLContext(mock(BotService.class), botInfo, user))
        .isInstanceOf(BotApiException.class)
        .hasMessageContaining("Both chatId and userId are null");
  }
}
