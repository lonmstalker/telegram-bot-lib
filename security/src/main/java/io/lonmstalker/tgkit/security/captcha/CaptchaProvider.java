package io.lonmstalker.tgkit.security.captcha;

import io.lonmstalker.tgkit.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Провайдер CAPTCHA. Возвращает готовое сообщение с вопросом и клавиатурой
 * и проверяет ответ пользователя.
 */
public interface CaptchaProvider {

    /**
     * Формирует сообщение-вопрос для указанного чата.
     *
     * @return {@link PartialBotApiMethod}
     */
    @NonNull
    PartialBotApiMethod<?> question(@NonNull BotRequest<?> request);

    /**
     * Проверяет ответ пользователя.
     *
     * @param answer ответ пользователя
     * @return {@code true}, если ответ верен
     */
    boolean verify(@NonNull BotRequest<?> request, @NonNull String answer);
}
