package io.lonmstalker.tgkit.core.exception;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface BotExceptionHandler {
    @Nullable
    BotApiMethod<?> handle(@NonNull Update update, @NonNull Exception ex);
}
