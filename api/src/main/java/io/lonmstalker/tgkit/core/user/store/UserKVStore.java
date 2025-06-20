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
package io.lonmstalker.tgkit.core.user.store;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Контракт key-value-хранилища «данные пользователя» */
public interface UserKVStore {

  @Nullable String get(long userId, @NonNull String key);

  @NonNull Map<String, String> getAll(long userId);

  void put(long userId, @NonNull String key, @NonNull String value);

  void remove(long userId, @NonNull String key);

  default boolean contains(long userId, @NonNull String key) {
    return get(userId, key) != null;
  }
}
