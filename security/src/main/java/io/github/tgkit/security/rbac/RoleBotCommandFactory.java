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

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.loader.BotCommandFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class RoleBotCommandFactory implements BotCommandFactory<RequiresRole> {

  @Override
  public @NonNull Class<RequiresRole> annotationType() {
    return RequiresRole.class;
  }

  @Override
  public void apply(
      @NonNull BotCommand<?> command, @NonNull Method method, @Nullable RequiresRole ann) {

    var anns =
        Arrays.stream(method.getAnnotationsByType(RequiresRole.class)).collect(Collectors.toSet());
    anns.addAll(Arrays.asList(method.getDeclaringClass().getAnnotationsByType(RequiresRole.class)));

    if (anns.isEmpty()) {
      return;
    }

    var roles = anns.stream().flatMap(a -> Arrays.stream(a.value())).collect(Collectors.toSet());

    command.addInterceptor(new RoleInterceptor(roles));
  }
}
