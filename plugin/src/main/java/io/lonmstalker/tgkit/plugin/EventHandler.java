package io.lonmstalker.tgkit.plugin;

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
    void handle(T event);
}
