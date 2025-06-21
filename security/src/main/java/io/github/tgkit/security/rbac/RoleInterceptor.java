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

import io.github.tgkit.api.BotRequest;
import io.github.tgkit.api.BotResponse;
import io.github.tgkit.api.interceptor.BotInterceptor;
import io.github.tgkit.api.user.BotUserInfo;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

final class RoleInterceptor implements BotInterceptor {
  private final Set<String> allowed;

  RoleInterceptor(Set<String> allowed) {
    this.allowed = allowed;
  }

  @Override
  public void preHandle(@NonNull Update upd, @NonNull BotRequest<?> request) {
    BotUserInfo u = request.user();
    if (u.roles().stream().noneMatch(allowed::contains)) {
      throw new ForbiddenException("role required: " + allowed);
    }
  }

  @Override
  public void postHandle(@NonNull Update u, @NonNull BotRequest<?> request) {}

  @Override
  public void afterCompletion(
      @NonNull Update u,
      @NonNull BotRequest<?> req,
      @Nullable BotResponse r,
      @Nullable Exception e) {}
}
