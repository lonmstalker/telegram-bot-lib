package io.lonmstalker.tgkit.security.audit;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Closeable;

public interface AuditSink extends Closeable {

    /**
     * асинхронно - вызывается в worker-треде AuditBus
     */
    void emit(@NonNull AuditEvent ev) throws Exception;

    /**
     * low-bps fallback
     */
    default void close() {
    }
}