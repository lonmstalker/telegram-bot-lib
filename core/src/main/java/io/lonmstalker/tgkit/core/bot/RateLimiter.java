package io.lonmstalker.tgkit.core.bot;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class RateLimiter {
    private final int permitsPerSecond;
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    private final boolean schedulerProvided;

    RateLimiter(int permitsPerSecond) {
        this(permitsPerSecond, null);
    }

    RateLimiter(int permitsPerSecond, @Nullable ScheduledExecutorService scheduler) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
        this.schedulerProvided = scheduler != null;
        this.scheduler = scheduler != null ? scheduler :
                Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = Executors.defaultThreadFactory().newThread(r);
                    t.setDaemon(true);
                    t.setName("tgkit-rate-limiter");
                    return t;
                });
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

    void close() {
        if (!schedulerProvided) {
            scheduler.shutdownNow();
        }
    }
}
