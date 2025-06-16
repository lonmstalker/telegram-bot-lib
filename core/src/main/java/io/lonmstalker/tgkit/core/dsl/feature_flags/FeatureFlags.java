package io.lonmstalker.tgkit.core.dsl.feature_flags;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Контракт проверки фич-флагов и A/B-тестов.
 */
public interface FeatureFlags {

    /**
     * Включён ли флаг для чата?
     */
    boolean enabled(@NonNull String key, long chatId);

    /**
     * Включён ли флаг для пользователя?
     */
    boolean enabledForUser(@NonNull String key, long userId);

    /**
     * Вариант (“control”/“variant”) для A/B-теста; null = не участвует.
     */
    @Nullable
    Variant variant(@NonNull String abKey, long entityId);

    enum Variant {CONTROL, VARIANT}
}
