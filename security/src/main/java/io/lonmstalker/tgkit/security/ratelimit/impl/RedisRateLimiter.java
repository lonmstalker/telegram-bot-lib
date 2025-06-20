package io.lonmstalker.tgkit.security.ratelimit.impl;

import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import org.checkerframework.checker.nullness.qual.NonNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 *
 *
 * <h2>Кластерный rate-limiter на Redis</h2>
 *
 * <p>Использует атомарный Lua-скрипт (INCR + EXPIRE) — производительность ≈ 1 мс, корректная работа
 * в Redis Cluster и на Replica-set.
 *
 * <h3>Lua-скрипт</h3>
 *
 * <pre>
 * local c = redis.call('INCR', KEYS[1])
 * if c == 1 then
 *     redis.call('EXPIRE', KEYS[1], ARGV[2])
 * end
 * return c &lt;= tonumber(ARGV[1]) and 1 or 0
 * </pre>
 *
 * <h3>Плюсы</h3>
 *
 * <ul>
 *   <li>Линейная масштабируемость — все экземпляры бота видят общий лимит.
 *   <li>Никаких блокировок / WATCH / MULTI — одна команда <i>eval</i>.
 * </ul>
 *
 * <h3>Минусы</h3>
 *
 * <ul>
 *   <li>Фиксированное окно (fixed window) — возможен burst на границе.
 *   <li>Нужен Redis server (stand-alone или кластер).
 * </ul>
 *
 * <h3>Пример подключения</h3>
 *
 * <pre>{@code
 * JedisPool pool = new JedisPool("redis.example", 6379);
 * RateLimiterBackend limiter = new RedisRateLimiter(pool);
 * }</pre>
 */
public final class RedisRateLimiter implements RateLimiter {

  /** Jedis connection pool, injected by DI framework. */
  private final JedisPool pool;

  public RedisRateLimiter(@NonNull JedisPool pool) {
    this.pool = pool;
  }

  /*───────────────────────────────────────────────────────────────*
   *  Atomic Lua (INCR + optional EXPIRE)                           *
   *  KEYS[1]   – bucket key                                        *
   *  ARGV[1]   – permits                                           *
   *  ARGV[2]   – ttlSeconds                                        *
   *  Returns   – 1 (boolean true) if allowed, else 0               *
   *───────────────────────────────────────────────────────────────*/
  private static final String LUA_SCRIPT =
      """
        local c = redis.call('INCR', KEYS[1])
        if c == 1 then
            redis.call('EXPIRE', KEYS[1], tonumber(ARGV[2]))
        end
        if c <= tonumber(ARGV[1]) then
            return 1
        else
            return 0
        end
        """;

  /**
   * Пытается зарезервировать {@code 1} токен в Redis.
   *
   * @param key базовый ключ (без окна)
   * @param permits макс. количество токенов
   * @param seconds длительность окна
   * @return {@code true} — лимит не превышен
   */
  @Override
  public boolean tryAcquire(@NonNull String key, int permits, int seconds) {
    if (seconds <= 0 || permits <= 0) return true;

    long window = (System.currentTimeMillis() / 1_000L) / seconds;
    String bucket = key + ':' + window;

    try (Jedis jedis = pool.getResource()) {
      Object r =
          jedis.eval(LUA_SCRIPT, 1, bucket, String.valueOf(permits), String.valueOf(seconds));

      /* Lua boolean → Long 1/0 */
      return Long.valueOf(1).equals(r);
    }
  }
}
