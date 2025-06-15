package io.lonmstalker.tgkit.core.dsl;

/**
 * Проверяет, включена ли фича для чата.
 */
public interface FeatureFlags {

    /**
     * @param key  название фичи
     * @param chat идентификатор чата
     * @return {@code true}, если включено
     */
    boolean enabled(String key, long chat);

    /**
     * Реализация по умолчанию, всегда возвращает {@code false}.
     */
    static FeatureFlags noop() {
        return (k, c) -> false;
    }
}
