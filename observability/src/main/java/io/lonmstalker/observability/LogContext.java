package io.lonmstalker.observability;

import org.slf4j.MDC;

public final class LogContext {
    private LogContext() {}

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static void clear() {
        MDC.clear();
    }
}
