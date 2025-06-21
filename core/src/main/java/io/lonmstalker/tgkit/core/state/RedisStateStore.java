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

package io.github.tgkit.core.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Реализация {@link StateStore} на Redis через простые операции GET/SET.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * JedisPool pool = new JedisPool("localhost", 6379);
 * StateStore store = new RedisStateStore(pool);
 * }</pre>
 */
public class RedisStateStore implements StateStore {

  private final JedisPool pool;

  public RedisStateStore(@NonNull JedisPool pool) {
    this.pool = pool;
  }

  @Override
  public @Nullable String get(@NonNull String chatId) {
    try (Jedis jedis = pool.getResource()) {
      return jedis.get(chatId);
    }
  }

  @Override
  public void set(@NonNull String chatId, @NonNull String value) {
    try (Jedis jedis = pool.getResource()) {
      jedis.set(chatId, value);
    }
  }
}
