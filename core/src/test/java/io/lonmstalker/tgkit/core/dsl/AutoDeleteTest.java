package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;

/** Проверка автУдаления сообщений. */
public class AutoDeleteTest {
    @Test
    void schedulesDelete() {
        FakeTransport tg = new FakeTransport();
        BotDSL.msg(TestUtils.request(1), "bye").chat(1).ttl(Duration.ofMinutes(2)).send(tg);
        assertThat(tg.ttls).hasSize(1);
    }
}
