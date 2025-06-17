package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.BotInfo;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MissingIdStrategyTest {

    @Test
    void errorStrategyThrows() {
        DSLContext ctx = ctxWithNullChat();
        MessageBuilder b = new MessageBuilder(ctx, "hi")
                .missingIdStrategy(MissingIdStrategy.ERROR);

        assertThatThrownBy(b::requireChatId)
                .isInstanceOfAny(RuntimeException.class, BotApiException.class);
    }

    @Test
    void warnStrategyDoesNotThrow() {
        DSLContext ctx = ctxWithNullChat();
        MessageBuilder b = new MessageBuilder(ctx, "hi")
                .missingIdStrategy(MissingIdStrategy.WARN);

        assertThatCode(b::requireChatId).doesNotThrowAnyException();
    }

    @Test
    void ignoreStrategySilent() {
        DSLContext ctx = ctxWithNullChat();
        MessageBuilder b = new MessageBuilder(ctx, "hi")
                .missingIdStrategy(MissingIdStrategy.IGNORE);

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
