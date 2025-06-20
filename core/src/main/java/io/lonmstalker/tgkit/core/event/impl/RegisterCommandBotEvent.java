package io.lonmstalker.tgkit.core.event.impl;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.event.BotEvent;
import java.lang.reflect.Method;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public record RegisterCommandBotEvent(
    @NonNull Instant timestamp,
    @Nullable Method method,
    @NonNull BotCommand<? extends BotApiObject> command)
    implements BotEvent {}
