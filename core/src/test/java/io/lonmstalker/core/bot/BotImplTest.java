package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class BotImplTest {

    static class TestSender extends TelegramSender {
        TestSender() {
            super(new DefaultBotOptions(), "token");
        }

        @Override
        @SuppressWarnings("unchecked")
        protected <T extends Serializable, Method extends BotApiMethod<T>> T sendApiMethod(Method method) throws TelegramApiException {
            if (method instanceof GetMe) {
                User user = new User();
                user.setId(123L);
                user.setUserName("tester");
                return (T) user;
            }
            return null;
        }

        @Override
        protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(Method method) {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Test
    void startShouldLoadUser() {
        var bot = BotImpl.builder()
                .id(1)
                .token("token")
                .config(new BotConfig())
                .absSender(new TestSender())
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.start();
        assertTrue(bot.isStarted());
        assertEquals("tester", bot.username());
    }

    @Test
    void accessWithoutStartShouldThrow() {
        var bot = BotImpl.builder()
                .id(1)
                .token("token")
                .config(new BotConfig())
                .absSender(new TestSender())
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        assertThrows(BotApiException.class, bot::externalId);
    }
}
