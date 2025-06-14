package io.lonmstalker.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface BotAdapter {
    @Nullable
    BotApiMethod<?> handle(@NonNull Update update);
}
