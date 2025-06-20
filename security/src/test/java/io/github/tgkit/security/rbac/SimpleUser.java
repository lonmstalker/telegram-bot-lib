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
package io.github.tgkit.security.rbac;

import io.github.tgkit.api.user.BotUserInfo;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

/* helper: минимальная имплементация BotUserInfo */
record SimpleUser(Set<String> roles) implements BotUserInfo {

  public Long chatId() {
    return 1L;
  }

  public Long userId() {
    return 1L;
  }

  @Override
  public @Nullable Long internalUserId() {
    return 0L;
  }
}
