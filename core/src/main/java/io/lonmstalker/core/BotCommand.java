package io.lonmstalker.core;

import io.lonmstalker.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface BotCommand<T extends BotApiObject> {

    @Nullable
    BotResponse handle(@NonNull BotRequest<T> request);

    @NonNull BotRequestType type();

    @NonNull CommandMatch<T> matcher();

    default @NonNull String bot() {
        return "";
    }

    default int order() {
        return BotCommandOrder.LAST;
    }
}
