package io.lonmstalker.tgkit.core.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotRegistry {
    public static final BotRegistry INSTANCE = new BotRegistry();
    private final Set<Bot> bots = ConcurrentHashMap.newKeySet();

    @NonNull
    public Collection<Bot> all() {
        return List.copyOf(bots);
    }

    @NonNull
    public Optional<Bot> getByInternalId(long internalId) {
        return bots.stream()
                .filter(bot -> bot.internalId() == internalId)
                .findFirst();
    }

    @NonNull
    public Optional<Bot> getByExternalId(long externalId) {
        return bots.stream()
                .filter(bot -> bot.externalId() == externalId)
                .findFirst();
    }

    void register(@NonNull Bot bot) {
        bots.add(bot);
    }

    void unregister(@NonNull Bot bot) {
        bots.remove(bot);
    }
}
