package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Bot {

    long internalId() throws BotApiException;

    long externalId() throws BotApiException;

    void start() throws BotApiException;

    void stop() throws BotApiException;

    String username() throws BotApiException;

    BotCommandRegistry registry();

    boolean isStarted();

    void onComplete(@NonNull BotCompleteAction action);
}
