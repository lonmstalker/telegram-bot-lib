package io.github.tgkit.testkit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.api.BotAdapter;
import io.github.tgkit.internal.bot.BotConfig;
import io.github.tgkit.internal.bot.TelegramSender;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

class UpdateInjectorTest {

  static class CaptureAdapter implements BotAdapter {
    final AtomicReference<Update> last = new AtomicReference<>();

    @Override
    public BotApiMethod<?> handle(@NonNull Update update) {
      last.set(update);
      return null;
    }
  }

  static class NoopSender extends TelegramSender {
    NoopSender() {
      super(BotConfig.builder().build(), "T");
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> @NonNull T execute(
        @NonNull Method method) {
      return null;
    }
  }

  @Test
  void dispatchBuildsUpdateSequentialIds() {
    CaptureAdapter adapter = new CaptureAdapter();
    NoopSender sender = new NoopSender();
    UpdateInjector injector = new UpdateInjector(adapter, sender);

    injector.text("hi").from(10L).dispatch();
    Update first = adapter.last.get();
    assertThat(first.getUpdateId()).isEqualTo(1);
    assertThat(first.getMessage().getChatId()).isEqualTo(10L);

    injector.text("again").from(10L).dispatch();
    Update second = adapter.last.get();
    assertThat(second.getUpdateId()).isEqualTo(2);
  }
}
