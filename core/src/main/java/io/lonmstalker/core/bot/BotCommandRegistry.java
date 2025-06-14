package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequestType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface BotCommandRegistry {

    void add(@NonNull BotCommand<?> command);

    <T extends BotApiObject> @Nullable BotCommand<T> find(@NonNull BotRequestType type,
                                                          @NonNull String bot,
                                                          @NonNull T data);
}
