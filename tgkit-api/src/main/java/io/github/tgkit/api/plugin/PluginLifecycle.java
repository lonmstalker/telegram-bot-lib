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
package io.github.tgkit.api.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 *
 * <h3>PluginLifecycle</h3>
 *
 * <p>Минимальный контракт, который должен реализовать каждый JAR-плагин.
 *
 * <ul>
 *   <li>{@link #onLoad(BotPluginContext)} — вызывается сразу после того, как PluginManager
 *       подгрузил JAR и сформировал контекст.
 *   <li>{@link #onUnload()} — всегда вызывается перед выгрузкой ClassLoader’а (hot-reload,
 *       shutdown). Освободите ресурсы, отмените задачи.
 * </ul>
 *
 * <p>❗ Не блокируйте поток внутри этих методов. Для I/O используйте
 */
public interface PluginLifecycle {

  /** Инициализация плагина. */
  default void onLoad(@NonNull BotPluginContext ctx) throws Exception {}

  /** Корректное завершение работы, освобождение ресурсов. */
  default void onUnload() throws Exception {}

  /** Hook перед остановкой плагина, для подготовки. */
  default void beforeStop() throws Exception {}

  /** Hook после stop, для финальной очистки. */
  default void afterStop() throws Exception {}
}
