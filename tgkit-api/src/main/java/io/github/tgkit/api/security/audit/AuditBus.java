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
package io.github.tgkit.api.security.audit;

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
