package io.lonmstalker.tgkit.core.event.impl;

import io.lonmstalker.tgkit.core.event.TelegramBotEvent;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public record StartStatusBotEvent(
    long botInternalId,
    long botExternalId,
    @NonNull Instant timestamp,
    @Nullable Throwable throwable)
    implements TelegramBotEvent {}
