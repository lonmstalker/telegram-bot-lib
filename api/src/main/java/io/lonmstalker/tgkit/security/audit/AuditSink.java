package io.lonmstalker.tgkit.security.audit;

import java.io.Closeable;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface AuditSink extends Closeable {

  /** асинхронно - вызывается в worker-треде AuditBus */
  void emit(@NonNull AuditEvent ev) throws Exception;

  /** low-bps fallback */
  default void close() {}
}
