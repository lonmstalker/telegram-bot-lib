package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * Обёртка над обновлением Telegram, содержащая дополнительную информацию о боте
 * и пользователе.
 *
 * @param updateId идентификатор обновления
 * @param data     данные обновления
 * @param botInfo  сведения о боте
 * @param user     информация о пользователе
 * @param <T>      тип данных обновления
 */
public record BotRequest<T>(int updateId,
                            @NonNull T data,
                            @NonNull BotInfo botInfo,
                            @NonNull BotUserInfo user,
                            @NonNull Locale locale) {
}
