package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Bot {

    long internalId() throws BotApiException;

    long externalId() throws BotApiException;

    void start() throws BotApiException;

    void stop() throws BotApiException;

    @NonNull
    String username() throws BotApiException;

    @NonNull
    BotCommandRegistry registry();

    boolean isStarted();

    void onComplete(@NonNull BotCompleteAction action);

    @NonNull
    BotConfig config();

    @NonNull
    String token();
}
