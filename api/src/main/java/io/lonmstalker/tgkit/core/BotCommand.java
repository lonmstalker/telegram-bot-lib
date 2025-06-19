package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

/**
 * Обработчик конкретной команды бота.
 *
 * <p><b>Стабильность:</b> API считается стабильным и совместимым между версиями.</p>
 *
 * @param <T> тип объекта Telegram API, с которым работает обработчик
 */
public interface BotCommand<T extends BotApiObject> {

    /**
     * Выполняет обработку запроса пользователя.
     *
     * @param request запрос, содержащий данные об обновлении
     * @return {@link BotResponse}, который необходимо отправить пользователю,
     *         либо {@code null}, если ответ не требуется
     */
    @Nullable
    BotResponse handle(@NonNull BotRequest<T> request);

    /**
     * Тип обрабатываемого запроса.
     *
     * @return тип запроса
     */
    @NonNull BotRequestType type();

    /**
     * Определяет правила сопоставления команды с обновлением.
     *
     * @return {@link CommandMatch} для проверки обновления
     */
    @NonNull CommandMatch<T> matcher();

    /**
     * Группа обработчика. Используется для объединения команд.
     *
     * @return название группы
     */
    default @NonNull String botGroup() {
        return "";
    }

    /**
     * Порядок выполнения команды.
     *
     * @return число, определяющее порядок
     */
    default int order() {
        return BotCommandOrder.LAST;
    }
}
