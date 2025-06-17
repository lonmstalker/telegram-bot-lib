package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.BotInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageBuilderTest {

    private DSLContext ctx;

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
        DslGlobalConfig.INSTANCE.markdownV2().sanitizeMarkdown();

        SendMessage msg = (SendMessage) new MessageBuilder(ctx, "*bold*")
                .parseMode(ParseMode.MARKDOWN_V2)
                .build();

        assertThat(msg.getChatId()).isEqualTo("555");
        assertThat(msg.getParseMode()).isEqualTo("MarkdownV2");
        assertThat(msg.getText()).isEqualTo("\\*bold\\*");   // Markdown-экранировано
    }
}
