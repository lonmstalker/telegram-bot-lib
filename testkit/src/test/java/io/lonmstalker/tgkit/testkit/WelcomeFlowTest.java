package io.lonmstalker.tgkit.testkit;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

@TelegramBotTest
class WelcomeFlowTest {

  @Test
  void startCommandSendsWelcome(
      UpdateInjector injector, TelegramMockServer server, BotAdapterImpl adapter) throws Exception {
    adapter.registry().add(new StartCommand());
    injector.text("/start").from(42L).dispatch();
    RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
    assertThat(req).isNotNull();
    assertThat(req.path()).endsWith("/sendMessage");
    assertThat(req.body()).contains("Welcome");
  }

  private static class StartCommand implements BotCommand<Message> {
    @Override
    public BotResponse handle(@NonNull BotRequest<Message> request) {
      return BotResponse.builder().method(request.msg("Welcome").build()).build();
    }

    @Override
    public @NonNull BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public @NonNull CommandMatch<Message> matcher() {
      return msg -> "/start".equals(msg.getText());
    }
  }
}
