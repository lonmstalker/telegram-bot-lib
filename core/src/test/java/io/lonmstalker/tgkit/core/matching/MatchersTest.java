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
package io.lonmstalker.tgkit.core.matching;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.storage.BotRequestContextHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class MatchersTest {

  static {
    BotCoreInitializer.init();
  }

  @AfterEach
  void clear() {
    BotRequestContextHolder.clear();
  }

  @Test
  void messageTextMatch() {
    Message msg = new Message();
    msg.setText("hello");
    assertTrue(new MessageTextMatch("hello").match(msg));
    assertFalse(new MessageTextMatch("HELLO").match(msg));
    assertTrue(new MessageTextMatch("HELLO", true).match(msg));
  }

  @Test
  void messageContainsMatch() {
    Message msg = new Message();
    msg.setText("hello world");
    assertTrue(new MessageContainsMatch("world").match(msg));
    assertFalse(new MessageContainsMatch("WORLD").match(msg));
    assertTrue(new MessageContainsMatch("WORLD", true).match(msg));
  }

  @Test
  void messageRegexMatch() {
    Message msg = new Message();
    msg.setText("abc123");
    assertTrue(new MessageRegexMatch("[a-z]+\\d+").match(msg));
    assertFalse(new MessageRegexMatch("^123").match(msg));
  }

  @Test
  void alwaysMatch() {
    BotApiObject obj = Mockito.mock(BotApiObject.class);
    assertTrue(new AlwaysMatch<>().match(obj));
  }

  @Test
  void userRoleMatch() {
    BotUserProvider provider =
        u ->
            new BotUserInfo() {
              @Override
              public @Nullable Long chatId() {
                return 1L;
              }

              @Override
              public @Nullable Long userId() {
                return chatId();
              }

              @Override
              public @Nullable Long internalUserId() {
                return null;
              }

              @Override
              public @NonNull Set<String> roles() {
                return Set.of("ADMIN");
              }

              @Override
              public java.util.Locale locale() {
                return null;
              }
            };
    User tgUser = new User();
    tgUser.setId(1L);
    Update update = new Update();
    update.setMessage(new Message());
    update.getMessage().setFrom(tgUser);
    BotRequestContextHolder.setUpdate(update);
    UserRoleMatch<Message> match = new UserRoleMatch<>(provider, Set.of("ADMIN"));
    assertTrue(match.match(update.getMessage()));
    UserRoleMatch<Message> matchFalse = new UserRoleMatch<>(provider, Set.of("USER"));
    assertFalse(matchFalse.match(update.getMessage()));
  }
}
