package io.lonmstalker.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.junit.jupiter.api.Test;

class TelegramSenderRateLimiterTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void permitsPerSecond() throws Exception {
    TelegramSenderRateLimiter limiter = new TelegramSenderRateLimiter(2);
    long start = System.currentTimeMillis();
    limiter.acquire();
    limiter.acquire();
    limiter.acquire();
    long elapsed = System.currentTimeMillis() - start;
    assertTrue(elapsed >= 1000);
    limiter.close();
    Field field = TelegramSenderRateLimiter.class.getDeclaredField("scheduler");
    field.setAccessible(true);
  }

  @Test
  void externalSchedulerNotClosed() {
    ScheduledExecutorService external = Executors.newSingleThreadScheduledExecutor();
    TelegramSenderRateLimiter limiter = new TelegramSenderRateLimiter(1, external);
    limiter.close();
    assertFalse(external.isShutdown());
    external.shutdownNow();
  }
}
