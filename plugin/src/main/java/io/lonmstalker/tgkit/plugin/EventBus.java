package io.lonmstalker.tgkit.plugin;

/**
 * Простая шина сообщений между плагинами.
 */
public interface EventBus {

    /**
     * Подписывает обработчик на сообщения.
     *
     * @param handler обработчик сообщений
     */
    void subscribe(MessageHandler handler);

    /**
     * Публикует новое сообщение всем подписчикам.
     *
     * @param message текст сообщения
     */
    void publish(String message);

    /** Обработчик сообщений. */
    interface MessageHandler {
        /** Вызывается при получении сообщения. */
        void onMessage(String message);
    }
}
