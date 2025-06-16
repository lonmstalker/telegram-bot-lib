package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Обработчик события шины.
 */
@FunctionalInterface
public interface EventHandler<T> {

    /**
     * Вызывается при получении события.
     *
     * @param event событие
     */
    void handle(@NonNull T event);
}
