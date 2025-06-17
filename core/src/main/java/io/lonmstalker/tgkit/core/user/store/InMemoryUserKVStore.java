package io.lonmstalker.tgkit.core.user.store;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryUserKVStore implements UserKVStore {
    private final Map<Long, Map<String, String>> data = new ConcurrentHashMap<>();

    @Override
    public String get(long userId, @NonNull String key) {
        return data.getOrDefault(userId, new ConcurrentHashMap<>()).get(key);
    }

    @Override
    public @NonNull Map<String, String> getAll(long userId) {
        return Map.copyOf(data.getOrDefault(userId, new ConcurrentHashMap<>()));
    }

    @Override
    public void put(long userId, @NonNull String key, @NonNull String val) {
        data.computeIfAbsent(userId, u -> new ConcurrentHashMap<>()).put(key, val);
    }

    @Override
    public void remove(long userId, @NonNull String key) {
        data.getOrDefault(userId, new ConcurrentHashMap<>()).remove(key);
    }
}
