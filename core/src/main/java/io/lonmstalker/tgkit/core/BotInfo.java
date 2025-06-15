package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.state.StateStore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Информация о работающем экземпляре бота.
 *
 * @param internalId внутренний идентификатор бота
 * @param store      хранилище пользовательского состояния
 * @param sender     объект для отправки сообщений Telegram
 * @param localizer  сервис локализации сообщений
 */
public record BotInfo(long internalId,
                      @NonNull StateStore store,
                      @NonNull TelegramSender sender,
                      @NonNull MessageLocalizer localizer) {
}
