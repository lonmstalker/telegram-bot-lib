/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.args;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.*;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.matching.MessageRegexMatch;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.bot.loader.AnnotatedCommandLoader;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizerImpl;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.store.InMemoryUserKVStore;
import java.util.Locale;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

public class ArgBindingTest {

  static {
    BotCoreInitializer.init();
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
}
