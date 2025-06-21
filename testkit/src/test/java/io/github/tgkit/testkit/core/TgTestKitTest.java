package io.github.tgkit.testkit.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.api.BotAdapter;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

class TgTestKitTest {

  @Test
  void sendAndExpect() {
    BotAdapter adapter =
        update -> new SendMessage(update.getMessage().getChatId().toString(), "pong");

    TgTestKit kit = new TgTestKit(adapter);
    kit.sendText(1L, "ping").expectReply("pong").end();
  }

  @Test
  void jsonSnapshot() throws Exception {
    BotAdapter adapter =
        update -> new SendMessage(update.getMessage().getChatId().toString(), "pong");
    TgTestKit kit = new TgTestKit(adapter);
    kit.sendText(1L, "ping").expectJsonSnapshot("snapshot.json").end();
  }
}
