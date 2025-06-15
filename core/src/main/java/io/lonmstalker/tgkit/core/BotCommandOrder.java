package io.lonmstalker.tgkit.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Константы, определяющие порядок выполнения обработчиков команд.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotCommandOrder {

    /** Выполняется первым. */
    public static final int FIRST = Integer.MIN_VALUE;

    /** Выполняется последним. */
    public static final int LAST = Integer.MAX_VALUE;
}
