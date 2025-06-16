package io.lonmstalker.tgkit.core.dsl.feature_flags;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Хранит состояние флагов в памяти.
 */
public class InMemoryFeatureFlags implements FeatureFlags {
    private final Map<String, Set<Long>> chatFlags = new ConcurrentHashMap<>();
    private final Map<String, Set<Long>> userFlags = new ConcurrentHashMap<>();
    private final Map<String, Integer> percentRoll = new ConcurrentHashMap<>(); // 0..100

    /* --- обычные флаги --- */
    public void enableChat(@NonNull String key, long chat) {
        chatFlags.computeIfAbsent(key, k -> new HashSet<>()).add(chat);
    }

    public void enableUser(@NonNull String key, long user) {
        userFlags.computeIfAbsent(key, k -> new HashSet<>()).add(user);
    }

    /* --- процентный rollout --- */
    public void rollout(@NonNull String key, int percent) {
        percentRoll.put(key, percent);
    }

    /* --- реализация интерфейса --- */
    @Override
    public boolean enabled(@NonNull String key, long chatId) {
        return chatFlags.getOrDefault(key, Set.of()).contains(chatId)
                || percentRoll.getOrDefault(key, 0) >= (chatId % 100);
    }

    @Override
    public boolean enabledForUser(@NonNull String key, long userId) {
        return userFlags.getOrDefault(key, Set.of()).contains(userId)
                || percentRoll.getOrDefault(key, 0) >= (userId % 100);
    }

    @Override
    public @Nullable Variant variant(@NonNull String key, long entityId) {
        Integer pct = percentRoll.get(key);
        if (pct == null) {
            return null;
        }
        return (entityId % 100) < pct ? Variant.VARIANT : Variant.CONTROL;
    }
}
