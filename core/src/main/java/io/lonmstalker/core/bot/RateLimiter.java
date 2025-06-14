package io.lonmstalker.core.bot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class RateLimiter {
    private final int permitsPerSecond;
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;

    RateLimiter(int permitsPerSecond) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(this::replenish, 1, 1, TimeUnit.SECONDS);
    }

    void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    private void replenish() {
        int diff = permitsPerSecond - semaphore.availablePermits();
        if (diff > 0) {
            semaphore.release(diff);
        }
    }
}
