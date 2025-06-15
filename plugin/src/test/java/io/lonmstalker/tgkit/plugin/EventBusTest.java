package io.lonmstalker.tgkit.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

public class EventBusTest {

    @Test
    public void publishAndSubscribe() {
        SimpleEventBus bus = new SimpleEventBus();
        AtomicInteger counter = new AtomicInteger();
        bus.subscribe(String.class, s -> counter.incrementAndGet());
        bus.publish("hi");
        assertEquals(1, counter.get());
    }
}
