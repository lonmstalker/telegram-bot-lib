package io.lonmstalker.core;

import io.lonmstalker.core.bot.TelegramSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public record BotInfo(long internalId,
                      @NonNull TelegramSender sender) {
}
