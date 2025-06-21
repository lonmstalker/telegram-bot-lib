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
package io.github.tgkit.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.tgkit.core.event.impl.StartStatusBotEvent;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class InMemoryEventBusTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void delivers_to_multiple_subscribers_and_unsubscribe() throws Exception {
    InMemoryEventBus bus = new InMemoryEventBus(Executors.newSingleThreadExecutor(), 10);
    try {
      List<BotEvent> all = new ArrayList<>();
      List<StartStatusBotEvent> starts = new ArrayList<>();

      BotEventSubscription allSub = bus.subscribe(BotEvent.class, all::add);
      BotEventSubscription startSub = bus.subscribe(StartStatusBotEvent.class, starts::add);

      StartStatusBotEvent first = new StartStatusBotEvent(1L, 2L, Instant.now(), null);
      bus.publish(first);

      Awaitility.await()
          .untilAsserted(
              () -> {
                assertThat(all).containsExactly(first);
                assertThat(starts).containsExactly(first);
              });

      bus.unsubscribe(startSub);

      StartStatusBotEvent second = new StartStatusBotEvent(1L, 2L, Instant.now(), null);
      bus.publish(second);

      Awaitility.await()
          .untilAsserted(
              () -> {
                assertThat(all).containsExactly(first, second);
                assertThat(starts).containsExactly(first);
              });

      bus.unsubscribe(allSub);
    } finally {
      bus.shutdown();
    }
  }

  @Test
  void queue_overflow_throws_rejected_execution() throws Exception {
    InMemoryEventBus bus = new InMemoryEventBus(Executors.newSingleThreadExecutor(), 1);
    try {
      bus.publish(new StartStatusBotEvent(1L, 2L, Instant.now(), null));
      assertThrows(
          RejectedExecutionException.class,
          () -> bus.publish(new StartStatusBotEvent(1L, 2L, Instant.now(), null)));
    } finally {
      bus.shutdown();
    }
  }
}
