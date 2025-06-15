package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Проверка работы фичевых флагов. */
public class FeatureFlagTest {

    @Test
    void toggleFlag() {
        StubFeatureFlags flags = new StubFeatureFlags();
        BotResponse.config(c -> c.featureFlags(flags));
        FakeContext ctx = new FakeContext(1L, Set.of());
        FakeTransport tg = new FakeTransport();

        BotResponse.msg("hi")
                .chat(1)
                .ifFlag("new", ctx, b -> b.keyboard(k -> k.row(Button.cb("N", "n"))))
                .send(tg);
        assertThat(tg.sent).hasSize(1);
        SendMessage msg1 = (SendMessage) tg.sent.get(0);
        assertThat(msg1.getReplyMarkup()).isNull();

        flags.enable("new", 1L);
        tg.sent.clear();
        BotResponse.msg("hi")
                .chat(1)
                .ifFlag("new", ctx, b -> b.keyboard(k -> k.row(Button.cb("N", "n"))))
                .send(tg);
        SendMessage msg2 = (SendMessage) tg.sent.get(0);
        assertThat(msg2.getReplyMarkup()).isNotNull();
    }
}
