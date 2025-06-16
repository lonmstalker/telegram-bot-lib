package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранит состояние флагов в памяти.
 */
public class InMemoryFeatureFlags implements FeatureFlags {
    private final Map<String, Set<Long>> flags = new ConcurrentHashMap<>();

    @Override
    public boolean enabled(@NonNull String key, long chat) {
        return flags.getOrDefault(key, Set.of()).contains(chat);
    }

    /** Включает флаг. */
    public void enable(@NonNull String key, long chat) {
        flags.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(chat);
    }

    /** Выключает флаг. */
    public void disable(@NonNull String key, long chat) {
        Set<Long> set = flags.get(key);
        if (set != null) {
            set.remove(chat);
        }
    }
}
