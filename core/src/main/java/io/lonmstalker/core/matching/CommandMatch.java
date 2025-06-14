package io.lonmstalker.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public interface CommandMatch<T extends BotApiObject> {
    boolean match(@NonNull T data);
}
