package io.craftbot.security;

import io.craftbot.security.spi.Key;
import io.craftbot.security.spi.RateLimitBackend;

public class RateLimiter {
    private final RateLimitBackend backend;

    public RateLimiter(RateLimitBackend backend) {
        this.backend = backend;
    }

    public boolean tryAcquire(Key key, int permits, int seconds) {
        return backend.tryAcquire(key, permits, java.time.Duration.ofSeconds(seconds));
    }
}
