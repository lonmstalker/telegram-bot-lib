package io.github.tgkit.testkit.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

class FakeTransportTest {

  @Test
  void emitsUpdates() {
    FakeTransport transport = new FakeTransport();
    Update u = new Update();
    transport.emit(u);
    Update received = transport.updates().blockFirst(Duration.ofSeconds(1));
    assertThat(received).isSameAs(u);
  }
}
