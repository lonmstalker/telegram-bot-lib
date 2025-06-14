package io.lonmstalker.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotCommandOrder {
    public static final int FIRST = Integer.MIN_VALUE;
    public static final int LAST = Integer.MAX_VALUE;
}
