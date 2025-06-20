package io.lonmstalker.tgkit.core.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import redis.clients.jedis.JedisPool;

/**
 * Хранилище состояний, использующее Redis через {@link JedisPool}.
 *
 * <p>Пример подключения:
 *
 * <pre>{@code
 * JedisPool pool = new JedisPool("redis.example", 6379);
 * StateStore store = new RedisStateStore(pool);
 * }</pre>
 */
public class RedisStateStore implements StateStore {

  private static final String PREFIX = "state:";

  private final JedisPool pool;

  public RedisStateStore(@NonNull JedisPool pool) {
    this.pool = pool;
  }

  @Override
  public @Nullable String get(@NonNull String chatId) {
    try (var j = pool.getResource()) {
      return j.get(PREFIX + chatId);
    }
  }

  @Override
  public void set(@NonNull String chatId, @NonNull String value) {
    try (var j = pool.getResource()) {
      j.set(PREFIX + chatId, value);
    }
  }
}
