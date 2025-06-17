package io.lonmstalker.tgkit.core.interceptor;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
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
     * @param request сформированный запрос
     */
    void preHandle(@NonNull Update update,
                   @NonNull BotRequest<?> request);

    /**
     * Вызывается после основного обработчика, но до отправки ответа.
     *
     * @param update обработанное обновление
     * @param request сформированный запрос
     */
    void postHandle(@NonNull Update update,
                    @NonNull BotRequest<?> request);

    /**
     * Вызывается после завершения обработки обновления.
     *
     * @param update   обновление
     * @param request сформированный запрос
     * @param response сформированный ответ, может быть {@code null}
     * @param ex       исключение, возникшее при обработке, может быть {@code null}
     * @throws Exception любая ошибка, которую необходимо пробросить
     */
    void afterCompletion(@NonNull Update update,
                         @Nullable BotRequest<?> request,
                         @Nullable BotResponse response,
                         @Nullable Exception ex) throws Exception;
}
