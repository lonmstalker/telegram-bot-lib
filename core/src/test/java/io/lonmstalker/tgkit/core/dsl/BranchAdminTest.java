package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Проверка onlyAdmin ветки. */
public class BranchAdminTest {
    @Test
    void adminBranchAddsButton() {
        FakeContext ctx = new FakeContext(1L, Set.of("ADMIN"));
        FakeTransport tg = new FakeTransport();
        BotResponse.msg("hi")
                .chat(1)
                .onlyAdmin(b -> b.keyboard(k -> k.row(Button.cb("A", "a"))))
                .send(tg);
        SendMessage msg = (SendMessage) tg.sent.get(0);
        assertThat(msg.getReplyMarkup()).isNotNull();
    }
}
