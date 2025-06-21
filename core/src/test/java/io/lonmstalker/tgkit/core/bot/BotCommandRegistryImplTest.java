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

package io.github.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.core.interceptor.BotInterceptor;
import io.github.tgkit.core.matching.CommandMatch;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

class BotCommandRegistryImplTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void add_and_find_commands() {
    BotCommandRegistryImpl registry = new BotCommandRegistryImpl();
    BotCommand<Message> first = new TestCommand(1);
    BotCommand<Message> second = new TestCommand(2);

    registry.add(first);
    registry.add(second);

    Message msg = new Message();
    BotCommand<Message> found = registry.find(BotRequestType.MESSAGE, "", msg);
    assertEquals(first, found);
  }

  @Test
  void find_wrong_type_throws() {
    BotCommandRegistryImpl registry = new BotCommandRegistryImpl();
    registry.add(new TestCommand(1));

    Update update = new Update();
    assertThrows(BotApiException.class, () -> registry.find(BotRequestType.MESSAGE, "", update));
  }

  private static class TestCommand implements BotCommand<Message> {
    private final int order;

    TestCommand(int order) {
      this.order = order;
    }

    @Override
    public BotResponse handle(@NonNull BotRequest<Message> request) {
      return null;
    }

    @Override
    public @NonNull BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public @NonNull CommandMatch<Message> matcher() {
      return data -> true;
    }

    @Override
    public @NonNull List<BotInterceptor> interceptors() {
      return List.of();
    }

    @Override
    public void setMatcher(@NonNull CommandMatch<Message> matcher) {
    }

    @Override
    public void setType(@NonNull BotRequestType type) {
    }

    @Override
    public void setBotGroup(@NonNull String group) {
    }

    @Override
    public int order() {
      return order;
    }

    @Override
    public @NonNull String botGroup() {
      return "";
    }
  }
}
