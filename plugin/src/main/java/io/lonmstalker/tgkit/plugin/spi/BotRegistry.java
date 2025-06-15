package io.lonmstalker.tgkit.plugin.spi;

import io.lonmstalker.tgkit.core.bot.Bot;
import java.util.Collection;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Реестр активных ботов.
 */
public interface BotRegistry {

    @NonNull
    Collection<Bot> all();

    @NonNull
    Optional<Bot> get(@NonNull String id);
}
