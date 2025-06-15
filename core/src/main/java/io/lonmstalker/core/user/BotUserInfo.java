package io.lonmstalker.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Set;
import java.util.Locale;

public interface BotUserInfo {

    @NonNull
    String chatId();

    @NonNull
    Set<String> roles();

    default @Nullable Locale locale() {
        return null;
    }
}
