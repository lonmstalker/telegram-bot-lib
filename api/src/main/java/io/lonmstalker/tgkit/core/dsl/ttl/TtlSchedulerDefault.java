/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.dsl.ttl;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.ttl.DeleteTask;
import io.lonmstalker.tgkit.core.ttl.TtlPolicy;
import io.lonmstalker.tgkit.core.ttl.TtlScheduler;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TtlSchedulerDefault implements TtlScheduler {
  private final Map<DeleteTask, RetryRunner> live = new ConcurrentHashMap<>();

  @Override
  public @NonNull CompletableFuture<Void> schedule(
      @NonNull DeleteTask task, @NonNull Duration delay, @NonNull TtlPolicy pol) {
    RetryRunner rr = new RetryRunner(task, pol);
    live.put(task, rr);
    rr.future =
        BotGlobalConfig.INSTANCE
            .executors()
            .getScheduledExecutorService()
            .schedule(rr, delay.toMillis(), TimeUnit.MILLISECONDS);
    return rr.promise;
  }

  @Override
  public void close() {
    BotGlobalConfig.INSTANCE.executors().getScheduledExecutorService().shutdown();
    try {
      BotGlobalConfig.INSTANCE
          .executors()
          .getScheduledExecutorService()
          .awaitTermination(2, TimeUnit.SECONDS);
    } catch (InterruptedException ignored) {
      Thread.currentThread().interrupt();
    }
  }

  private final class RetryRunner implements Runnable {
    final DeleteTask task;
    final TtlPolicy pol;
    final AtomicInteger attempts = new AtomicInteger();
    final CompletableFuture<Void> promise = new CompletableFuture<>();
    volatile ScheduledFuture<?> future;

    RetryRunner(@NonNull DeleteTask t, @NonNull TtlPolicy p) {
      task = t;
      pol = p;
    }

    @Override
    public void run() {
      try {
        task.action().run();
        promise.complete(null);
        live.remove(task);
      } catch (Exception ex) {
        int n = attempts.incrementAndGet();
        if (n > pol.maxRetries()) {
          promise.completeExceptionally(ex);
          live.remove(task);
          return;
        }
        long next =
            Math.min(
                pol.initialBackOff().toMillis() * (1L << (n - 1)), pol.maxBackOff().toMillis());
        future =
            BotGlobalConfig.INSTANCE
                .executors()
                .getScheduledExecutorService()
                .schedule(this, next, TimeUnit.MILLISECONDS);
      }
    }
  }
}
