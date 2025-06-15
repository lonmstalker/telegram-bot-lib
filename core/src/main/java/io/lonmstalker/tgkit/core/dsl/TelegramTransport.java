package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Интерфейс транспорта Telegram.
 */
public interface TelegramTransport {

    /**
     * Отправляет метод Telegram API.
     *
     * @return идентификатор сообщения
     */
    long execute(@NonNull PartialBotApiMethod<?> method);

    /** Удаляет сообщение. */
    void delete(long chatId, long messageId);

    /** Планирует удаление сообщения. */
    default void scheduleDelete(long chatId, long messageId, @NonNull Duration ttl) {}
}
