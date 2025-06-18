package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.assertj.core.api.Assertions.*;

class AuditBusTest {

    /* отдельный executor, чтобы не мешать другим тестам + контролируем shutdown */
    private AsyncAuditBus bus;
    private ExecutorService exec;

    static {
        BotCoreInitializer.init();
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
                .untilAsserted(() -> {
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

        Awaitility.await().atMost(200, TimeUnit.MILLISECONDS)
                .until(() -> counter.get() == 1);

        bus.unsubscribe(handler);
        bus.publish(AuditEvent.userAction(2, "second"));

        /* ждём и убеждаемся, что счётчик не увеличился */
        Awaitility.await().during(Duration.ofMillis(200))
                .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
    }

    @Test
    @DisplayName("исключение одного подписчика не ломает остальных")
    void faultySubscriberDoesNotBreakOthers() {
        AtomicBoolean goodCalled = new AtomicBoolean(false);

        bus.subscribe(ev -> {
            throw new RuntimeException("boom");
        });
        bus.subscribe(ev -> goodCalled.set(true));

        bus.publish(AuditEvent.securityAlert("system", "check"));

        Awaitility.await().atMost(200, TimeUnit.MILLISECONDS)
                .untilTrue(goodCalled);
    }

    @Test
    @DisplayName("горячая подмена bus через AuditGlobalConfig")
    void hotSwapBus() {
        BotSecurityGlobalConfig.AuditGlobalConfig cfg = new BotSecurityGlobalConfig.AuditGlobalConfig().bus(bus);

        /* новый backend, считаем события */
        AtomicInteger counter = new AtomicInteger();
        AsyncAuditBus newBus = new AsyncAuditBus(exec, 100);
        newBus.subscribe(ev -> counter.incrementAndGet());

        cfg.bus(newBus);                         // 🔄 hot-swap
        cfg.bus().publish(AuditEvent.userAction(3, "after-swap"));

        Awaitility.await().atMost(200, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> assertThat(counter.get()).isEqualTo(1));
    }
}
