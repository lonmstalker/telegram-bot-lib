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
package io.github.tgkit.core.user.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class InMemoryUserKVStore implements UserKVStore {
  private final Map<Long, Map<String, String>> data = new ConcurrentHashMap<>();

  @Override
  public String get(long userId, @NonNull String key) {
    return data.getOrDefault(userId, new ConcurrentHashMap<>()).get(key);
  }

  @Override
  public @NonNull Map<String, String> getAll(long userId) {
    return Map.copyOf(data.getOrDefault(userId, new ConcurrentHashMap<>()));
  }

  @Override
  public void put(long userId, @NonNull String key, @NonNull String val) {
    data.computeIfAbsent(userId, u -> new ConcurrentHashMap<>()).put(key, val);
  }

  @Override
  public void remove(long userId, @NonNull String key) {
    data.getOrDefault(userId, new ConcurrentHashMap<>()).remove(key);
  }
}
