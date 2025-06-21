package io.github.tgkit.testkit;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.api.BotCommand;
import io.github.tgkit.api.BotRequest;
import io.github.tgkit.api.BotRequestType;
import io.github.tgkit.api.BotResponse;
import io.github.tgkit.api.matching.CommandMatch;
import io.github.tgkit.internal.bot.BotAdapterImpl;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

@TelegramBotTest
class BotTestExtensionTest {

  @Test
  void extensionInjectsDependencies(
      UpdateInjector injector, TelegramMockServer server, BotAdapterImpl adapter)
      throws Exception {
    adapter.registry().add(new PingCommand());
    injector.text("/ping").from(1L).dispatch();
    RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
    assertThat(req).isNotNull();
    assertThat(req.path()).endsWith("/sendMessage");
    assertThat(req.body()).contains("pong");
  }

  private static class PingCommand implements BotCommand<org.telegram.telegrambots.meta.api.objects.Message> {
    @Override
    public BotResponse handle(@NonNull BotRequest<org.telegram.telegrambots.meta.api.objects.Message> request) {
      return BotResponse.builder().method(request.msg("pong").build()).build();
    }

    @Override
    public @NonNull BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public @NonNull CommandMatch<org.telegram.telegrambots.meta.api.objects.Message> matcher() {
      return msg -> "/ping".equals(msg.getText());
    }
  }
}
