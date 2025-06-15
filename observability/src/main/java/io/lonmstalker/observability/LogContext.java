package io.lonmstalker.observability;

import org.slf4j.MDC;

/**
 * Утилитарный класс для работы с MDC при логировании.
 */
public final class LogContext {
    private LogContext() {}

    /**
     * Помещает значение в контекст логирования.
     *
     * @param key   ключ MDC
     * @param value значение
     */
public final class LogContext {
    private LogContext() {}

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    /**
     * Очищает MDC.
     */
    public static void clear() {
        MDC.clear();
    }
}
