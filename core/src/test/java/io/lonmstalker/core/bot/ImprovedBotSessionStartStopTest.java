package io.lonmstalker.core.bot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ImprovedBotSession lifecycle")
class ImprovedBotSessionStartStopTest {

    static class DummyReceiver extends LongPollingReceiver {
        DummyReceiver() {
            super(new BotConfig(), update -> null, "t", null);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
            if (method instanceof GetMe) {
                User u = new User();
                u.setId(1L);
                u.setUserName("tester");
                return (T) u;
            }
            return null;
        }
    }

    @Test
    @DisplayName("session starts and stops")
    void sessionStartStop() {
        BotConfig cfg = new BotConfig();
        DummyReceiver receiver = new DummyReceiver();
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("t")
                .config(cfg)
                .absSender(receiver)
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.start();
        ImprovedBotSession session = getSession(bot);
        assertNotNull(session);
        assertTrue(session.isRunning());
        bot.stop();
        assertFalse(session.isRunning());
    }

    private ImprovedBotSession getSession(BotImpl bot) {
        try {
            var f = BotImpl.class.getDeclaredField("session");
            f.setAccessible(true);
            return (ImprovedBotSession) f.get(bot);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
