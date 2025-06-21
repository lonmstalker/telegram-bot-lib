package io.github.tgkit.testkit.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.api.BotAdapter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class TgTestKitTest {

  static class EchoAdapter implements BotAdapter {
    final AtomicReference<String> lastText = new AtomicReference<>();

    @Override
    public BotApiMethod<?> handle(@NonNull Update update) {
      String txt = update.getMessage().getText();
      lastText.set(txt);
      return new SendMessage(update.getMessage().getChatId().toString(), txt);
    }
  }

  @Test
  void dslSendsAndVerifiesText() {
    EchoAdapter adapter = new EchoAdapter();
    TgTestKit.with(adapter).sendText("/ping").expectReply("/ping").end();
    assertThat(adapter.lastText.get()).isEqualTo("/ping");
  }

  @Test
  void jsonSnapshotMatches() throws IOException {
    EchoAdapter adapter = new EchoAdapter();
    var kit = TgTestKit.with(adapter).sendText("hi");
    kit.expectJsonSnapshot("/expected-send-message.json");
  }
}
