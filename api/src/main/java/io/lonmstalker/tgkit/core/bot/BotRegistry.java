package io.lonmstalker.tgkit.core.bot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;

public interface BotRegistry {

    @NonNull
    Collection<Bot> all();

    @NonNull
    Optional<Bot> getByInternalId(long internalId);

    @NonNull
    Optional<Bot> getByExternalId(long externalId);
}
