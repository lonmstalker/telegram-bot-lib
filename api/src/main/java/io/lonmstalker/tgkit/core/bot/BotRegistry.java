package io.lonmstalker.tgkit.core.bot;

import java.util.Collection;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BotRegistry {

  @NonNull Collection<Bot> all();

  @NonNull Optional<Bot> getByInternalId(long internalId);

  @NonNull Optional<Bot> getByExternalId(long externalId);
}
