package io.lonmstalker.tgkit.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;
import java.util.Locale;

public interface BotUserInfo {

    @Nullable
    Long chatId();

    @Nullable
    Long userId();

    @Nullable
    Long internalUserId();

    @NonNull
    Set<String> roles();

    default @Nullable Locale locale() {
        return null;
    }
}
