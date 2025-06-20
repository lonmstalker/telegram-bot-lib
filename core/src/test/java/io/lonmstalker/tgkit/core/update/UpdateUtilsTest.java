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
package io.lonmstalker.tgkit.core.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.*;

public class UpdateUtilsTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void getTypeMessage() {
    Update update = new Update();
    update.setMessage(new Message());
    assertEquals(BotRequestType.MESSAGE, UpdateUtils.getType(update));
  }

  @Test
  void getTypeCallbackQuery() {
    Update update = new Update();
    update.setCallbackQuery(new CallbackQuery());
    assertEquals(BotRequestType.CALLBACK_QUERY, UpdateUtils.getType(update));
  }

  @Test
  void getTypeUnknownThrows() {
    Update update = new Update();
    assertThrows(BotApiException.class, () -> UpdateUtils.getType(update));
  }

  @Test
  void getUserFromCallback() {
    User user = new User();
    user.setId(1L);
    CallbackQuery cq = new CallbackQuery();
    cq.setFrom(user);
    Update update = new Update();
    update.setCallbackQuery(cq);
    assertEquals(user, UpdateUtils.getUser(update));
  }
}
