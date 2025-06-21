package io.github.tgkit.testkit;


import io.github.tgkit.api.BotCommand;
import io.github.tgkit.api.BotRequest;
import io.github.tgkit.api.BotRequestType;
import io.github.tgkit.api.BotResponse;
import io.github.tgkit.api.matching.CommandMatch;
import io.github.tgkit.internal.bot.BotAdapterImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@TelegramBotTest
class ExpectationTest {

  @Test
  void verifiesApiRequest(
      UpdateInjector inject, Expectation expect, BotAdapterImpl adapter) {
    adapter.registry().add(new PingCommand());
    inject.text("/ping").from(1L).dispatch();
    expect.api("sendMessage").jsonPath("$.text", "pong");
  }

  private static class PingCommand implements BotCommand<Message> {
    @Override
    public BotResponse handle(@NonNull BotRequest<Message> request) {
      SendMessage msg = new SendMessage(request.msg().getChatId().toString(), "pong");
      return BotResponse.builder().method(msg).build();
    }

    @Override
    public @NonNull BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public @NonNull CommandMatch<Message> matcher() {
      return m -> "/ping".equals(m.getText());
    }
  }
}
