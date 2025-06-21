/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
  void pingCommandVerified(UpdateInjector inject, BotAdapterImpl adapter, Expectation expect) {
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
