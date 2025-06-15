package io.lonmstalker.tgkit.plugin;

/**
 * Уведомление о подписке на событие. Позволяет отписаться.
 */
public interface Subscription extends AutoCloseable {
    @Override
    void close();
}
