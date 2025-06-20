package io.lonmstalker.tgkit.core.ttl;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.*;

class TtlSchedulerTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  @DisplayName("task runs exactly once after delay")
  void executesTaskOnce() throws Exception {
    AtomicInteger calls = new AtomicInteger();

    DeleteTask task = new DeleteTask(1, 100, calls::incrementAndGet);

    BotGlobalConfig.INSTANCE
        .dsl()
        .getTtlScheduler()
        .schedule(task, Duration.ofMillis(150))
        .get(1, TimeUnit.SECONDS);

    assertThat(calls).hasValue(1);
  }

  @Test
  @DisplayName("exception completes future exceptionally")
  void exceptionPropagates() {
    DeleteTask task =
        new DeleteTask(
            1,
            1,
            () -> {
              throw new IllegalStateException("boom");
            });

    CompletableFuture<Void> cf =
        BotGlobalConfig.INSTANCE
            .dsl()
            .getTtlScheduler()
            .schedule(task, Duration.ZERO, noRunPolicy());

    assertThat(cf).isCompletedExceptionally();
  }

  private TtlPolicy noRunPolicy() {
    return new TtlPolicy() {

      @Override
      public int maxRetries() {
        return 0;
      }

      @Override
      public @NonNull Duration initialBackOff() {
        return Duration.ZERO;
      }

      @Override
      public @NonNull Duration maxBackOff() {
        return Duration.ZERO;
      }
    };
  }
}
