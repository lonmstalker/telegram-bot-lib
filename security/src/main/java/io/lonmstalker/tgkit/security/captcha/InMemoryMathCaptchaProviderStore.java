package io.lonmstalker.tgkit.security.captcha;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;

public class InMemoryMathCaptchaProviderStore implements MathCaptchaProviderStore {
    private final Cache<Long, Integer> cache;

    public InMemoryMathCaptchaProviderStore(Duration ttl, long maxSize) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(maxSize)
                .build();
    }

    public void put(long c, int a, @NonNull Duration __) {
        cache.put(c, a);
    }

    public Integer pop(long c) {
        return cache.asMap().remove(c);
    }
}
