package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.security.antispam.AntiSpamInterceptor;
import io.lonmstalker.tgkit.security.antispam.DuplicateProvider;
import io.lonmstalker.tgkit.security.antispam.InMemoryDuplicateProvider;
import io.lonmstalker.tgkit.security.captcha.*;
import io.lonmstalker.tgkit.security.captcha.provider.MathCaptchaProvider;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import io.lonmstalker.tgkit.security.ratelimit.impl.InMemoryRateLimiter;
import io.lonmstalker.tgkit.security.ratelimit.impl.RedisRateLimiter;
import java.time.Duration;
import java.util.Set;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import redis.clients.jedis.JedisPool;

/**
 * Утилита для создания типовых компонентов безопасности: проверка на дубликаты, CAPTCHA и
 * ограничители скорости.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * RateLimiter limiter = BotSecurity.inMemoryRateLimiter();
 * AntiSpamInterceptor interceptor = BotSecurity.antiSpamInterceptor(Set.of("example.com"));
 * }</pre>
 */
public final class BotSecurity {

  private BotSecurity() {}

  /** Создаёт хранитель дубликатов сообщений в памяти. */
  public static @NonNull DuplicateProvider inMemoryDuplicateProvider(
      @NonNull Duration ttl, long maxSize) {
    return InMemoryDuplicateProvider.builder().ttl(ttl).maxSize(maxSize).build();
  }

  /** Математическая CAPTCHA с хранением в памяти. */
  public static @NonNull CaptchaProvider inMemoryCaptchaProvider(
      @NonNull Duration ttl, long maxSize) {
    return MathCaptchaProvider.builder()
        .wrongCount(1)
        .ttl(ttl)
        .store(new InMemoryMathCaptchaProviderStore(ttl, maxSize))
        .numberRange(Range.of(5, 25))
        .allowedOps(MathCaptchaOperations.OPERATIONS)
        .build();
  }

  /** Математическая CAPTCHA с хранением в Redis. */
  public static @NonNull CaptchaProvider redisCaptchaProvider(
      @NonNull Duration ttl, @NonNull JedisPool pool) {
    return MathCaptchaProvider.builder()
        .wrongCount(1)
        .ttl(ttl)
        .numberRange(Range.of(5, 25))
        .allowedOps(MathCaptchaOperations.OPERATIONS)
        .store(new RedisMathCaptchaProviderStore(pool))
        .build();
  }

  /** Простейший ограничитель скорости в памяти. */
  public static @NonNull RateLimiter inMemoryRateLimiter() {
    return new InMemoryRateLimiter(1000);
  }

  /** Ограничитель скорости на базе Redis. */
  public static @NonNull RateLimiter redisRateLimiter(@NonNull JedisPool pool) {
    return new RedisRateLimiter(pool);
  }

  /** Упрощённый билдёр для {@link AntiSpamInterceptor}. */
  public static @NonNull AntiSpamInterceptor antiSpamInterceptor(@NonNull Set<String> badDomains) {
    return AntiSpamInterceptor.builder()
        .badDomains(badDomains)
        .flood(inMemoryRateLimiter())
        .dup(inMemoryDuplicateProvider(Duration.ofSeconds(10), 10))
        .captcha(inMemoryCaptchaProvider(Duration.ofSeconds(30), 100))
        .build();
  }
}
