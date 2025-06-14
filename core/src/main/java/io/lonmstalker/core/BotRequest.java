package io.lonmstalker.core;

import io.lonmstalker.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

public record BotRequest<T>(int updateId,
                            @NonNull T data,
                            @NonNull BotInfo botInfo,
                            @NonNull BotUserInfo user) {
}
