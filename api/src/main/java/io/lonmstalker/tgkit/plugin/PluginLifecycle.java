package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * <h3>PluginLifecycle</h3>
 * <p>
 * Минимальный контракт, который должен реализовать каждый JAR-плагин.
 *
 * <ul>
 *   <li>{@link #onLoad(BotPluginContext)} — вызывается сразу после того, как
 *       PluginManager подгрузил JAR и сформировал контекст.</li>
 *   <li>{@link #onUnload()} — всегда вызывается перед выгрузкой ClassLoader’а
 *       (hot-reload, shutdown). Освободите ресурсы, отмените задачи.</li>
 * </ul>
 *
 * <p>❗ Не блокируйте поток внутри этих методов. Для I/O используйте
 */
public interface PluginLifecycle {

    /**
     * Инициализация плагина.
     */
    default void onLoad(@NonNull BotPluginContext ctx) throws Exception {

    }

    /**
     * Корректное завершение работы, освобождение ресурсов.
     */
    default void onUnload() throws Exception {

    }
}