package io.lonmstalker.tgkit.security.ratelimit;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface RateLimiter {

  boolean tryAcquire(@NonNull String key, int permits, int seconds);
}
