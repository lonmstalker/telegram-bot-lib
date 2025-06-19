package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequestType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.List;

public interface BotCommandRegistry {

    /**
     * Возвращает список всех зарегистрированных команд (неизменяемый).
     *
     * @return список команд
     */
    @NonNull
    List<BotCommand<?>> all();

    /**
     * Регистрирует новую команду и сортирует список по приоритету.
     *
     * @param command экземпляр команды
     */
    void add(@NonNull BotCommand<?> command);

    /**
     * Ищет первую команду, подходящую под тип, группу и matcher.
     *
     * @param type     тип запроса
     * @param botGroup группа команд (любая == пустая строка)
     * @param data     объект Telegram API
     * @param <T>      тип объекта
     * @return команда или {@code null}, если не найдена
     */
    <T extends BotApiObject> @Nullable BotCommand<T> find(@NonNull BotRequestType type,
                                                          @NonNull String botGroup,
                                                          @NonNull T data);
}
