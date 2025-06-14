package io.lonmstalker.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface BotRequestConverter<T> {
    @NonNull T convert(@NonNull Update update, @NonNull BotRequestType type);
}
