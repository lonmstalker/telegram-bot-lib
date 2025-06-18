package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

class TelegramSenderRateLimiter {
    private final int permitsPerSecond;
    private final Semaphore semaphore;
    private final ScheduledExecutorService scheduler;
    private final ScheduledFuture<?> scheduledFuture;

    TelegramSenderRateLimiter(int permitsPerSecond) {
        this(permitsPerSecond, null);
    }

    TelegramSenderRateLimiter(int permitsPerSecond,
                              @Nullable ScheduledExecutorService scheduler) {
        this.permitsPerSecond = permitsPerSecond;
        this.semaphore = new Semaphore(permitsPerSecond);
        this.scheduler = scheduler != null
                ? scheduler
                : BotGlobalConfig.INSTANCE.executors().getScheduledExecutorService();
        this.scheduledFuture =
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
        scheduledFuture.cancel(true);
    }
}
