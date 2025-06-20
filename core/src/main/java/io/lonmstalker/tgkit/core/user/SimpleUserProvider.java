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
package io.lonmstalker.tgkit.core.user;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Простая реализация {@link BotUserProvider}, вытягивающая идентификаторы пользователя и чата из {@link Update}.
 */
public class SimpleUserProvider implements BotUserProvider {

  @Override
  public @NonNull BotUserInfo resolve(@NonNull Update update) {
    Long userId = UpdateUtils.resolveUserId(update);
    Long chatId = UpdateUtils.resolveChatId(update);
    return new SimpleBotUserInfo(userId, chatId);
  }

  static class SimpleBotUserInfo implements BotUserInfo {
    private @Nullable Long userId;
    private @Nullable Long chatId;

    SimpleBotUserInfo(@Nullable Long userId, @Nullable Long chatId) {
      this.userId = userId;
      this.chatId = chatId;
    }

    @Override
    public @Nullable Long chatId() {
      return chatId;
    }

    @Override
    public @Nullable Long userId() {
      return userId;
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
