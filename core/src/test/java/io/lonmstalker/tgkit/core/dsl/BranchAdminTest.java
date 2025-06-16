package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Проверка onlyAdmin ветки. */
public class BranchAdminTest {
    @Test
    void adminBranchAddsButton() {
        Context ctx = new Context(1L, Set.of("ADMIN"));
        FakeTransport tg = new FakeTransport();
        BotDSL.msg(TestUtils.request(1), "hi")
                .chat(1)
                .onlyAdmin(b -> b.keyboard(k -> k.row(Button.cb("A", "a"))))
                .send(tg);
        SendMessage msg = (SendMessage) tg.sent.get(0);
        assertThat(msg.getReplyMarkup()).isNotNull();
    }
}
