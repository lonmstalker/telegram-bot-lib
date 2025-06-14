package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.Serializable;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TelegramSenderTest {

    static class ErrorTelegramSender extends TelegramSender {
        ErrorTelegramSender() {
            super(new DefaultBotOptions(), "token");
        }

        @Override
        public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
            throw new BotApiException("error");
        }

        @Override
        protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(Method method) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new TelegramApiRequestException("error"));
            return future;
        }
    }

    @Test
    void executeShouldConvertException() {
        ErrorTelegramSender sender = new ErrorTelegramSender();
        assertThrows(BotApiException.class, () -> sender.execute(new GetMe()));
    }

    @Test
    void executeAsyncShouldConvertException() {
        ErrorTelegramSender sender = new ErrorTelegramSender();
        assertThrows(BotApiException.class, () -> sender.executeAsync(new GetMe()).join());
    }
}
