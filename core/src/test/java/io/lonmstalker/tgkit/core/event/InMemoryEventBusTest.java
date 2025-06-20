package io.lonmstalker.tgkit.core.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.tgkit.core.event.impl.StartStatusBotEvent;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

class InMemoryEventBusTest {

  static {
    BotCoreInitializer.init();
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
