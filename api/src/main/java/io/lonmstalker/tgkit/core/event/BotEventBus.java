package io.lonmstalker.tgkit.core.event;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Шина событий бота.
 *
 * <p><b>Стабильность:</b> API планируется стабильным, но пока может измениться.
 */
public interface BotEventBus {

  /** Публикует событие *синхронно* (throw-on-error). */
  <E extends BotEvent> void publish(@NonNull E event);

  /** Публикует *асинхронно* (не блокирует продюсера). */
  <E extends BotEvent> @NonNull CompletableFuture<Void> publishAsync(@NonNull E event);

  /** Подписка на тип E. Возвращает handle для detach(). */
  <E extends BotEvent> @NonNull BotEventSubscription subscribe(
      @NonNull Class<E> type, @NonNull Consumer<E> handler);

  /** Отписка вручную (optional). */
  void unsubscribe(@NonNull BotEventSubscription s);

  /** Кол-во событий, которые ещё в queue. */
  int backlog();

  /** Graceful-shutdown: дренируем очередь и ждём, когда consumers исчезнут. */
  void shutdown() throws InterruptedException;
}
