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
package io.github.tgkit.internal.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Простейшее потокобезопасное хранилище {@link StateStore} на базе {@link ConcurrentHashMap}.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * StateStore store = new InMemoryStateStore();
 * store.set("42", "step1");
 * }</pre>
 */
public class InMemoryStateStore implements StateStore {
  private final Map<String, String> store = new ConcurrentHashMap<>();

  @Override
  public @Nullable String get(@NonNull String chatId) {
    return store.get(chatId);
  }

  @Override
  public void set(@NonNull String chatId, @NonNull String value) {
    store.put(chatId, value);
  }
}
