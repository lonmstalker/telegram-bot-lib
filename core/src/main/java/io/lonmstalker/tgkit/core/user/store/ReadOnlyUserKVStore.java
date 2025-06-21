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
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ReadOnlyUserKVStore implements UserKVStore {
  private final @NonNull UserKVStore delegate;

  public ReadOnlyUserKVStore(@NonNull UserKVStore delegate) {
    this.delegate = delegate;
  }

  @Override
  public String get(long uid, @NonNull String k) {
    return delegate.get(uid, k);
  }

  @Override
  public @NonNull Map<String, String> getAll(long uid) {
    return delegate.getAll(uid);
  }

  @Override
  public void put(long uid, @NonNull String k, @NonNull String v) {
    throw new UnsupportedOperationException("read-only");
  }

  @Override
  public void remove(long uid, @NonNull String k) {
    throw new UnsupportedOperationException("read-only");
  }
}
