package io.lonmstalker.core;

import io.lonmstalker.core.bot.TelegramSender;
import io.lonmstalker.core.state.StateStore;
import org.checkerframework.checker.nullness.qual.NonNull;

public record BotInfo(long internalId,
                      @NonNull StateStore store,
                      @NonNull TelegramSender sender) {
}
