package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotAdapter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotAdapterImpl implements BotAdapter {

    @Override
    public @Nullable BotApiMethod<?> handle(@NonNull Update update) {
        return null;
    }
}
