package io.lonmstalker.tgkit.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBackend implements RateLimiterBackend {
    private final Map<String, Entry> counters = new ConcurrentHashMap<>();

    public static InMemoryBackend create() {
        return new InMemoryBackend();
    }

    @Override
    public boolean tryAcquire(String key, int permits, int seconds) {
        long now = Instant.now().getEpochSecond();
        Entry e = counters.compute(key, (k, old) -> {
            if (old == null || now - old.timestamp >= seconds) {
                return new Entry(1, now);
            } else {
                return new Entry(old.count + 1, old.timestamp);
            }
        });
        return e.count <= permits;
    }

    private record Entry(int count, long timestamp) {}
}
