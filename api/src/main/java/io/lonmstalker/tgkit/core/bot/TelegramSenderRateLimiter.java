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
package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;

class TelegramSenderRateLimiter {
  private final int permitsPerSecond;
  private final Semaphore semaphore;
  private final ScheduledExecutorService scheduler;
  private final ScheduledFuture<?> scheduledFuture;

  TelegramSenderRateLimiter(int permitsPerSecond) {
    this(permitsPerSecond, null);
  }

  TelegramSenderRateLimiter(int permitsPerSecond, @Nullable ScheduledExecutorService scheduler) {
    this.permitsPerSecond = permitsPerSecond;
    this.semaphore = new Semaphore(permitsPerSecond);
    this.scheduler =
        scheduler != null
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
