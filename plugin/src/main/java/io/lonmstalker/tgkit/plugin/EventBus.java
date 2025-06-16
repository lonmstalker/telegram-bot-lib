package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Простая шина сообщений между плагинами.
 */
public interface EventBus {

    /**
     * Подписывает обработчик на сообщения.
     *
     * @param handler обработчик сообщений
     */
    void subscribe(@NonNull MessageHandler handler);

    /**
     * Публикует новое сообщение всем подписчикам.
     *
     * @param message текст сообщения
     */
    void publish(@NonNull String message);

    /** Обработчик сообщений. */
    interface MessageHandler {
        /** Вызывается при получении сообщения. */
        void onMessage(@NonNull String message);
    }
}
