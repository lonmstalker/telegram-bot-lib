package io.lonmstalker.core.bot;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class BotSessionLifecycleTest {

    static class DummySession extends BotSessionImpl {
        boolean started;
        boolean stopped;

        @Override
        public synchronized void start() {
            this.started = true;
        }

        @Override
        public synchronized void stop() {
            this.stopped = true;
        }

        @Override
        public boolean isRunning() {
            return started && !stopped;
        }
    }

    static class TestReceiver extends LongPollingReceiver {
        TestReceiver() {
            super(new BotConfig(), update -> null, "token", null);
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

        @Override
        protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(Method method) {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Test
    void startStartsSession() {
        DummySession session = new DummySession();
        TestReceiver receiver = new TestReceiver();
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("token")
                .config(new BotConfig())
                .absSender(receiver)
                .session(session)
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.start();
        assertTrue(session.started);
    }

    @Test
    void stopStopsSession() {
        DummySession session = new DummySession();
        TestReceiver receiver = new TestReceiver();
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("token")
                .config(new BotConfig())
                .absSender(receiver)
                .session(session)
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.start();
        bot.stop();
        assertTrue(session.stopped);
    }
}

