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
package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.assertj.core.api.Assertions.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class AuditBusTest {

  /* отдельный executor, чтобы не мешать другим тестам + контролируем shutdown */
  private AsyncAuditBus bus;
  private ExecutorService exec;
  private final Logger logger = (Logger) LoggerFactory.getLogger(AsyncAuditBus.class);
  private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

  static {
    TestBotBootstrap.initOnce();
    BotSecurityInitializer.init();
  }

  @BeforeEach
  void init() {
    exec = Executors.newSingleThreadExecutor();
    bus = new AsyncAuditBus(exec, 100);
  }

  @AfterEach
  void tearDown() {
    exec.shutdownNow();
    logger.detachAppender(appender);
  }

  /* ------------------------------------------------------------------ */

  @Test
  @DisplayName("publish доставляет событие всем подписчикам")
  void publishDelivered() {
    AtomicInteger calls1 = new AtomicInteger();
    AtomicInteger calls2 = new AtomicInteger();

    bus.subscribe(ev -> calls1.incrementAndGet());
    bus.subscribe(ev -> calls2.incrementAndGet());

    bus.publish(AuditEvent.userAction(1, "test"));

    Awaitility.await()
        .atMost(Duration.ofMillis(200))
        .untilAsserted(
            () -> {
              assertThat(calls1.get()).isEqualTo(1);
              assertThat(calls2.get()).isEqualTo(1);
            });
  }

  @Test
  @DisplayName("unsubscribe прекращает доставку")
  void unsubscribeStopsDelivery() {
    AtomicInteger counter = new AtomicInteger();
    var handler = (java.util.function.Consumer<AuditEvent>) ev -> counter.incrementAndGet();

    bus.subscribe(handler);
    bus.publish(AuditEvent.userAction(2, "first"));

    Awaitility.await().atMost(200, TimeUnit.MILLISECONDS).until(() -> counter.get() == 1);

    bus.unsubscribe(handler);
    bus.publish(AuditEvent.userAction(2, "second"));

    /* ждём и убеждаемся, что счётчик не увеличился */
    Awaitility.await()
        .during(Duration.ofMillis(200))
        .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
  }

  @Test
  @DisplayName("исключение одного подписчика не ломает остальных")
  void faultySubscriberDoesNotBreakOthers() {
    AtomicBoolean goodCalled = new AtomicBoolean(false);

    bus.subscribe(
        ev -> {
          throw new RuntimeException("boom");
        });
    bus.subscribe(ev -> goodCalled.set(true));

    bus.publish(AuditEvent.securityAlert("system", "check"));

    Awaitility.await().atMost(200, TimeUnit.MILLISECONDS).untilTrue(goodCalled);
  }

  @Test
  @DisplayName("горячая подмена bus через AuditGlobalConfig")
  void hotSwapBus() {
    BotSecurityGlobalConfig.AuditGlobalConfig cfg =
        new BotSecurityGlobalConfig.AuditGlobalConfig().bus(bus);

    /* новый backend, считаем события */
    AtomicInteger counter = new AtomicInteger();
    AsyncAuditBus newBus = new AsyncAuditBus(exec, 100);
    newBus.subscribe(ev -> counter.incrementAndGet());

    cfg.bus(newBus); // 🔄 hot-swap
    cfg.bus().publish(AuditEvent.userAction(3, "after-swap"));

    Awaitility.await()
        .atMost(200, TimeUnit.MILLISECONDS)
        .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
  }

  @Test
  @DisplayName("очередь заполнена: событие отбрасывается и логируется")
  void dropOnFullBuffer() {
    logger.addAppender(appender);
    appender.start();

    bus.close();
    bus = new AsyncAuditBus(exec, 1);
    AtomicInteger delivered = new AtomicInteger();
    bus.subscribe(
        ev -> {
          delivered.incrementAndGet();
          try {
            Thread.sleep(100);
          } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
          }
        });

    bus.publish(AuditEvent.userAction(1, "first"));
    bus.publish(AuditEvent.userAction(1, "second"));

    Awaitility.await()
        .atMost(Duration.ofMillis(300))
        .untilAsserted(() -> assertThat(delivered.get()).isEqualTo(1));

    assertThat(bus.droppedCount()).isEqualTo(1);
    assertThat(appender.list).hasSize(1);

    logger.detachAppender(appender);
  }
}
