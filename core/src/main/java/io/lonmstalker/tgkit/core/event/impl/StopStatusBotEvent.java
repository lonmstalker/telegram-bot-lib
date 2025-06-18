package io.lonmstalker.tgkit.core.event.impl;

import io.lonmstalker.tgkit.core.event.TelegramBotEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Instant;

public record StopStatusBotEvent(long botInternalId,
                                 long botExternalId,
                                 @NonNull Instant timestamp,
                                 @Nullable Throwable throwable) implements TelegramBotEvent {
}
