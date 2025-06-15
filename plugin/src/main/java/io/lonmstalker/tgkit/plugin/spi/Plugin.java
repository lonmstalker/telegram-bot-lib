package io.lonmstalker.tgkit.plugin.spi;

/**
 * Контракт плагина. Минимальный ABI, чтобы ядро могло безопасно загружать
 * внешние расширения.
 */
public interface Plugin {

    /**
     * Версия ABI плагин-SPI. Позволяет ядру отвергать несовместимые плагины.
     */
    int abiVersion();

    /**
     * Инициализация плагина. Здесь следует только сохранять ссылки и
     * подписываться на события.
     */
    void init(PluginContext ctx) throws Exception;

    /** Запуск логики после инициализации. */
    void start() throws Exception;

    /** Чистое завершение работы. */
    void stop() throws Exception;
}
