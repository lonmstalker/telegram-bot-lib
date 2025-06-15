package io.lonmstalker.tgkit.core.bot;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.*;

class RateLimiterTest {

    @Test
    void shouldRespectPermitsPerSecondWhenAcquiring() throws Exception {
        RateLimiter limiter = new RateLimiter(2);
        long start = System.currentTimeMillis();
        limiter.acquire();
        limiter.acquire();
        limiter.acquire();
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(elapsed >= 1000);
        limiter.close();
        Field field = RateLimiter.class.getDeclaredField("scheduler");
        field.setAccessible(true);
        ScheduledExecutorService executor = (ScheduledExecutorService) field.get(limiter);
        assertTrue(executor.isShutdown());
    }

    @Test
    void shouldNotCloseExternalSchedulerWhenClosed() {
        ScheduledExecutorService external = Executors.newSingleThreadScheduledExecutor();
        RateLimiter limiter = new RateLimiter(1, external);
        limiter.close();
        assertFalse(external.isShutdown());
        external.shutdownNow();
    }
}
