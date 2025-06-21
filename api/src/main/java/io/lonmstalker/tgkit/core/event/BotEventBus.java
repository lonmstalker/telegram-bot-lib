/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.core.event;

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
