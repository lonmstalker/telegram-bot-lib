package io.lonmstalker.tgkit.core.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotRegistryImpl implements BotRegistry {
    public static final BotRegistryImpl INSTANCE = new BotRegistryImpl();
    private final Set<Bot> bots = ConcurrentHashMap.newKeySet();

    @NonNull
    @Override
    public Collection<Bot> all() {
        return List.copyOf(bots);
    }

    @NonNull
    @Override
    public Optional<Bot> getByInternalId(long internalId) {
        return bots.stream()
                .filter(bot -> bot.internalId() == internalId)
                .findFirst();
    }

    @NonNull
    @Override
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
