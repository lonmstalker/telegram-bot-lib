package io.lonmstalker.tgkit.security;

public interface RateLimiterBackend {
    boolean tryAcquire(String key, int permits, int seconds);
}
