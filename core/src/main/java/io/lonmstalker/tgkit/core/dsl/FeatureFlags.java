package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Проверяет, включена ли фича для чата.
 */
public interface FeatureFlags {

    /**
     * @param key  название фичи
     * @param chat идентификатор чата
     * @return {@code true}, если включено
     */
    boolean enabled(@NonNull String key, long chat);

    /**
     * Реализация по умолчанию, всегда возвращает {@code true}.
     */
    static FeatureFlags noop() {
        return (k, c) -> true;
    }
}
