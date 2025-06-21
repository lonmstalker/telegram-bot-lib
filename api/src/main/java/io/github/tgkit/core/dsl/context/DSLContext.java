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
package io.github.tgkit.core.dsl.context;

import io.github.tgkit.core.BotInfo;
import io.github.tgkit.core.BotService;
import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Контекст выполнения ответа. */
public interface DSLContext {

  @NonNull BotInfo botInfo();

  @NonNull BotUserInfo userInfo();

  @NonNull BotService service();

  /** Проверяет роль администратора. */
  boolean isAdmin();

  record SimpleDSLContext(
      @NonNull BotService service, @NonNull BotInfo botInfo, @NonNull BotUserInfo userInfo)
      implements DSLContext {

    public SimpleDSLContext {
      Long cId = userInfo.chatId();
      Long uId = userInfo.userId();
      if (cId == null && uId == null) {
        throw new BotApiException("Both chatId and userId are null in update");
      }
    }

    /** Проверяет роль администратора. */
    public boolean isAdmin() {
      return userInfo.roles().contains("ADMIN");
    }
  }
}
