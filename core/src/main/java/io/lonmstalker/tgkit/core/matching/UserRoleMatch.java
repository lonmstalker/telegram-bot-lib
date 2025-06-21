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

package io.github.tgkit.core.matching;

import io.github.tgkit.core.storage.BotRequestContextHolder;
import io.github.tgkit.core.user.BotUserInfo;
import io.github.tgkit.core.user.BotUserProvider;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Проверяет роль пользователя с помощью {@link BotUserProvider}.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * CommandMatch<?> match = new UserRoleMatch<>(provider, Set.of("ADMIN"));
 * }</pre>
 */
public class UserRoleMatch<T extends BotApiObject> implements CommandMatch<T> {

  private final BotUserProvider provider;
  private final Set<String> roles;

  public UserRoleMatch(@NonNull BotUserProvider provider, @NonNull Set<String> roles) {
    this.provider = provider;
    this.roles = Set.copyOf(roles);
  }

  @Override
  public boolean match(@NonNull T data) {
    Update update = BotRequestContextHolder.getUpdate();
    if (update == null) {
      return false;
    }
    BotUserInfo user = provider.resolve(update);
    return user.roles().stream().anyMatch(roles::contains);
  }
}
