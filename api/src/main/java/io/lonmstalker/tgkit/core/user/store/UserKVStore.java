package io.lonmstalker.tgkit.core.user.store;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;

/**
 * Контракт key-value-хранилища «данные пользователя»
 */
public interface UserKVStore {

    @Nullable
    String get(long userId, @NonNull String key);

    @NonNull
    Map<String, String> getAll(long userId);

    void put(long userId, @NonNull String key, @NonNull String value);

    void remove(long userId, @NonNull String key);

    default boolean contains(long userId, @NonNull String key) {
        return get(userId, key) != null;
    }
}