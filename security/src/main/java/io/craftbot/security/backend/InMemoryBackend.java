package io.craftbot.security.backend;

import io.craftbot.security.spi.Key;
import io.craftbot.security.spi.RateLimitBackend;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBackend implements RateLimitBackend {
    private final Map<String, Integer> counters = new ConcurrentHashMap<>();

    @Override
    public boolean tryAcquire(Key key, int permits, Duration window) {
        counters.merge(key.value(), 1, Integer::sum);
        return counters.get(key.value()) <= permits;
    }
}
