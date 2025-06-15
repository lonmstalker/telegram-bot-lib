package io.craftbot.security.spi;

import java.time.Duration;

public interface RateLimitBackend {
    boolean tryAcquire(Key key, int permits, Duration window);
}
