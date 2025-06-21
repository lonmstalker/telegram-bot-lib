package io.github.tgkit.testkit.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.api.BotAdapter;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class FakeTransportTest {

  static class EchoAdapter implements BotAdapter {
    final AtomicReference<Update> last = new AtomicReference<>();

    @Override
    public BotApiMethod<?> handle(@NonNull Update update) {
      last.set(update);
      Message msg = update.getMessage();
      return new SendMessage(msg.getChatId().toString(), msg.getText());
    }
  }

  @Test
  void dispatchStoresUpdateAndMethod() {
    EchoAdapter adapter = new EchoAdapter();
    FakeTransport transport = new FakeTransport(adapter);

    Message m = new Message();
    m.setText("hi");
    m.setChatId(1L);
    Update u = new Update();
    u.setMessage(m);

    transport.dispatch(u);

    assertThat(adapter.last.get()).isSameAs(u);
    assertThat(transport.updates()).containsExactly(u);
    assertThat(transport.methods()).hasSize(1);
    assertThat(transport.lastMethod()).isInstanceOf(SendMessage.class);
  }
}
