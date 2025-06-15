package io.lonmstalker.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Адаптер бота, выполняющий обработку входящих {@link Update} и формирующий
 * ответ в виде метода Telegram API.
 */
@FunctionalInterface
public interface BotAdapter {

    /**
     * Обработать входящее обновление Telegram.
     *
     * @param update полученное от Telegram обновление
     * @return метод Telegram API для отправки пользователю или {@code null}, если
     *         ответа не требуется
     */
    @Nullable
    BotApiMethod<?> handle(@NonNull Update update);
}
