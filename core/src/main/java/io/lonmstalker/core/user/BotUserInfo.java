package io.lonmstalker.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

public interface BotUserInfo {

    @NonNull
    String chatId();

    @NonNull
    Set<String> roles();
}
