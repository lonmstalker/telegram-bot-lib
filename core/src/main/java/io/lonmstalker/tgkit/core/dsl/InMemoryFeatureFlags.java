package io.lonmstalker.tgkit.core.dsl;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранит состояние флагов в памяти.
 */
public final class InMemoryFeatureFlags implements FeatureFlags {

    private final Map<String, Set<Long>> flags = new ConcurrentHashMap<>();

    @Override
    public boolean enabled(String key, long chat) {
        return flags.getOrDefault(key, Set.of()).contains(chat);
    }

    /** Включает флаг. */
    public void enable(String key, long chat) {
        flags.computeIfAbsent(key, k -> ConcurrentHashMap.newKeySet()).add(chat);
    }

    /** Выключает флаг. */
    public void disable(String key, long chat) {
        Set<Long> set = flags.get(key);
        if (set != null) {
            set.remove(chat);
        }
    }
}
