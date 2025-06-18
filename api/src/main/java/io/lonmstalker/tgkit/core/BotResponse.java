package io.lonmstalker.tgkit.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
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
    private @Nullable BotApiMethod<?> method;
}
