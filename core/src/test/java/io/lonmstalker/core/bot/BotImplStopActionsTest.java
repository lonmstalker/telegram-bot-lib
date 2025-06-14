package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotImpl stop actions")
class BotImplStopActionsTest {

    static class TestSender extends TelegramSender {
        TestSender() { super(new DefaultBotOptions(), "t"); }

        @Override
        @SuppressWarnings("unchecked")
        public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws BotApiException {
            if (method instanceof GetMe) {
                User u = new User();
                u.setId(1L);
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
    @DisplayName("stop вызывает зарегистрированные действия")
    void stopRunsActions() {
        AtomicInteger counter = new AtomicInteger();
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("t")
                .config(new BotConfig())
                .absSender(new TestSender())
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.onComplete(counter::incrementAndGet);
        bot.onComplete(counter::incrementAndGet);
        bot.start();
        bot.stop();
        assertEquals(2, counter.get());
        assertFalse(bot.isStarted());
    }
}
