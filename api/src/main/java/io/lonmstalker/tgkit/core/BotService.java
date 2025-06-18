package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.state.StateStore;
import io.lonmstalker.tgkit.core.user.store.UserKVStore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Сервисы для работы команд
 *
 * @param store       хранилище пользовательского состояния
 * @param sender      объект для отправки сообщений Telegram
 * @param userKVStore объект для хранения дополнительной информации пользователя
 * @param localizer   сервис локализации сообщений
 */
public record BotService(@NonNull StateStore store,
                         @NonNull TelegramSender sender,
                         @NonNull UserKVStore userKVStore,
                         @NonNull MessageLocalizer localizer) {
}
