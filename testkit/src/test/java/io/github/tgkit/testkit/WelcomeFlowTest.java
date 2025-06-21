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

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.bot.BotAdapterImpl;
import io.github.tgkit.core.matching.CommandMatch;
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
