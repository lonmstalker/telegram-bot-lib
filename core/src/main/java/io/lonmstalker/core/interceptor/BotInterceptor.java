package io.lonmstalker.core.interceptor;

import io.lonmstalker.core.BotResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Интерцептор обработки обновлений. Позволяет выполнить логику до и после
 * основного хендлера.
 */
public interface BotInterceptor {

    /**
     * Вызывается перед обработкой обновления.
     *
     * @param update полученное обновление
     */
    void preHandle(@NonNull Update update);

    /**
     * Вызывается после основного обработчика, но до отправки ответа.
     *
     * @param update обработанное обновление
     */
    void postHandle(@NonNull Update update);

    /**
     * Вызывается после завершения обработки обновления.
     *
     * @param update   обновление
     * @param response сформированный ответ, может быть {@code null}
     * @param ex       исключение, возникшее при обработке, может быть {@code null}
     * @throws Exception любая ошибка, которую необходимо пробросить
     */
    void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) throws Exception;
}
