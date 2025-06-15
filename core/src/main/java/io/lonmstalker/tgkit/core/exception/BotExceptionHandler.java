package io.lonmstalker.tgkit.core.exception;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface BotExceptionHandler {
    void handle(@NonNull Update update, @NonNull Exception ex);
}
