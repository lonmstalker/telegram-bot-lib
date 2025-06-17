package io.lonmstalker.tgkit.security.ratelimit.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <h2>In-process фиксированное окно («fixed-window counter»)</h2>
 *
 * <p>Каждый ключ-окно хранится в памяти JVM внутри {@link Caffeine}-кэша.
 * Счётчик реализован через {@link AtomicLong}, что делает операцию
 * {@link #tryAcquire(String, int, int)} lock-free
 * и&nbsp;O(1) по времени.</p>
 *
 * <h3>Алгоритм</h3>
 * <pre>{@code
 * window    = epochSeconds / seconds
 * bucketKey = originalKey + ':' + window
 * counter   = cache.computeIfAbsent(bucketKey, ...) ++
 * allowed   = counter ≤ permits
 * }</pre>
 *
 * <p>Для минимизации GC используется экспирация записей: бакет
 * хранится {@code windowTTL = 2 × seconds} и автоматически удаляется.</p>
 *
 * <h3>Потокобезопасность</h3>
 * Реализация не использует блокировок; {@link AtomicLong} обеспечивает
 * корректное ++ даже при тысячах параллельных потоков.
 *
 * <h3>Ограничения</h3>
 * <ul>
 *   <li>Лимиты действуют только внутри текущего процесса; при
 *       горизонтальном масштабировании используйте {@link RedisRateLimiter}.</li>
 *   <li>Алгоритм «fixed window» допускает небольшой burst на границе
 *       окон. Для сглаживания перейдите на скользящее окно или Token Bucket.</li>
 * </ul>
 */
public final class InMemoryRateLimiter implements RateLimiter {

    /**
     * value = AtomicLong counter for the current window
     */
    private final Cache<String, AtomicLong> buckets;

    /**
     * @param maxBuckets максимальное количество бакетов
     *                   (≈ ключей) в кэше. Ограничивает потребление RAM.
     */
    public InMemoryRateLimiter(long maxBuckets) {
        this.buckets = Caffeine.newBuilder()
                .maximumSize(maxBuckets)
                .expireAfterWrite(2, TimeUnit.DAYS)     // windows are short; 2 days more than enough
                .build();
    }

    /**
     * Попытка зарезервировать {@code 1} токен.
     *
     * @param key     логический ключ (без окна)
     * @param permits максимальное количество токенов в окне
     * @param seconds длительность окна
     * @return {@code true} — если токен получен; {@code false} — лимит исчерпан
     */
    @Override
    public boolean tryAcquire(@NonNull String key, int permits, int seconds) {
        long window = (System.currentTimeMillis() / 1000) / seconds;
        String bucketKey = key + ':' + window;

        /* lock-free counter */
        long current = Objects
                .requireNonNull(buckets.get(bucketKey, __ -> new AtomicLong()))
                .incrementAndGet();

        return current <= permits;
    }
}
