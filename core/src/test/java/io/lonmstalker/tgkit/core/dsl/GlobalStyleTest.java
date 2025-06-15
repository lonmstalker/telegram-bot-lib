package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Проверка глобального стиля сообщений. */
public class GlobalStyleTest {
    @Test
    void markdownSanitize() {
        BotResponse.config(c -> c.markdownV2().sanitizeMarkdown());
        SendMessage msg = (SendMessage) BotResponse.msg(TestUtils.request(1), "*").chat(1).build();
        assertThat(msg.getParseMode()).isEqualTo(ParseMode.MARKDOWNV2);
        assertThat(msg.getText()).contains("&ast;");
    }
}
