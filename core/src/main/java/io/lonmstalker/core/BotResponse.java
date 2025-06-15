package io.lonmstalker.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/**
 * Ответ бота в виде метода Telegram API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BotResponse {

    /** Метод, который будет выполнен Telegram API. */
    private BotApiMethod<?> method;
}
