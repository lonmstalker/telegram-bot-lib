package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Провайдер CAPTCHA. Возвращает готовое сообщение с вопросом и клавиатурой
 * и проверяет ответ пользователя.
 */
public interface CaptchaProvider {

    /**
     * Формирует сообщение-вопрос для указанного чата.
     *
     * @param chatId    чат, в который отправляется CAPTCHA
     * @param localizer локализатор сообщений
     * @return {@link SendMessage} с текстом и клавиатурой
     */
    SendMessage question(long chatId, MessageLocalizer localizer);

    /**
     * Проверяет ответ пользователя.
     *
     * @param chatId идентификатор чата
     * @param answer ответ пользователя
     * @return {@code true}, если ответ верен
     */
    boolean verify(long chatId, String answer);
}
