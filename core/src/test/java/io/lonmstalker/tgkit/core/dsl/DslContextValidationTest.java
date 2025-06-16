package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext.SimpleDSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.BotInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DslContextValidationTest {

    @Test
    void shouldThrowWhenBothIdsNull() {
        BotInfo botInfo = mock(BotInfo.class);
        BotUserInfo user = mock(BotUserInfo.class);
        when(user.chatId()).thenReturn(null);
        when(user.userId()).thenReturn(null);

        assertThatThrownBy(() -> new SimpleDSLContext(botInfo, user))
                .isInstanceOf(BotApiException.class)
                .hasMessageContaining("Both chatId and userId are null");
    }
}