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
package io.github.tgkit.internal.args;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.internal.*;
import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.BotRequestType;
import io.github.tgkit.internal.BotResponse;
import io.github.tgkit.internal.annotation.Arg;
import io.github.tgkit.internal.annotation.BotHandler;
import io.github.tgkit.internal.annotation.matching.MessageRegexMatch;
import io.github.tgkit.internal.bot.BotCommandRegistryImpl;
import io.github.tgkit.internal.bot.BotConfig;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.bot.loader.AnnotatedCommandLoader;
import io.github.tgkit.internal.i18n.MessageLocalizerImpl;
import io.github.tgkit.internal.state.InMemoryStateStore;
import io.github.tgkit.internal.user.BotUserInfo;
import io.github.tgkit.internal.user.store.InMemoryUserKVStore;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.util.Locale;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ArgBindingTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void bindArgument() {
    BotCommandRegistryImpl reg = new BotCommandRegistryImpl();
    AnnotatedCommandLoader.load(reg, Commands.class.getPackageName());

    Message msg = new Message();
    msg.setText("id 42");
    var cmd = reg.find(BotRequestType.MESSAGE, "", msg);
    assertNotNull(cmd);

    BotInfo info = new BotInfo(1L);
    BotService service =
        new BotService(
            new InMemoryStateStore(),
            new TelegramSender(BotConfig.builder().build(), "T"),
            new InMemoryUserKVStore(),
            new MessageLocalizerImpl("i18n/messages", Locale.US));
    BotRequest<Message> req =
        new BotRequest<>(
            0, msg, Locale.getDefault(), null, info, new User(1L), service, BotRequestType.MESSAGE);
    cmd.handle(req);

    assertEquals(42, Commands.captured);
  }

  public static class Commands {
    static volatile int captured;

    @BotHandler(type = BotRequestType.MESSAGE)
    @MessageRegexMatch("id (?<id>\\d+)")
    public BotResponse test(BotRequest<Message> req, @Arg("id") int id) {
      captured = id;
      return null;
    }
  }

  private record User(@Nullable Long chatId) implements BotUserInfo {

    @Override
    public @Nullable Long userId() {
      return chatId;
    }

    @Override
    public @Nullable Long internalUserId() {
      return null;
    }

    @Override
    public @NonNull Set<String> roles() {
      return Set.of();
    }
  }
}
