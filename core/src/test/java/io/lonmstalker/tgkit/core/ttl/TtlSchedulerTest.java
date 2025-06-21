/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.tgkit.core.ttl;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.*;

class TtlSchedulerTest {

  static {
    TestBotBootstrap.initOnce();
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
