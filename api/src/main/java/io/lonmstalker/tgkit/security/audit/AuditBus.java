package io.lonmstalker.tgkit.security.audit;

import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Единый контракт для публикации и подписки. */
public interface AuditBus extends AutoCloseable {

  /** Опубликовать событие (быстро, без блокировок). */
  void publish(@NonNull AuditEvent event);

  /** Зарегистрировать подписчика. */
  void subscribe(@NonNull Consumer<AuditEvent> handler);

  /** Убрать подписчика. */
  void unsubscribe(@NonNull Consumer<AuditEvent> handler);

  @NonNull List<Consumer<AuditEvent>> getSubscribers();
}
