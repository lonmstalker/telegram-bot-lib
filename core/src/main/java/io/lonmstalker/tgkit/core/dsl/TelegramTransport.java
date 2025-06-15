package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Интерфейс транспорта Telegram.
 */
public interface TelegramTransport {

    /**
     * Отправляет метод Telegram API.
     *
     * @return идентификатор сообщения
     */
    long execute(BotApiMethod<?> method);

    /** Удаляет сообщение. */
    void delete(long chatId, long messageId);

    /** Планирует удаление сообщения. */
    default void scheduleDelete(long chatId, long messageId, Duration ttl) {}
}
