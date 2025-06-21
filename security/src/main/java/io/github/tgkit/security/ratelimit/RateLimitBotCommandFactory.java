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

package io.github.tgkit.security.ratelimit;

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.loader.BotCommandFactory;
import io.github.tgkit.security.config.BotSecurityGlobalConfig;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Heavy-weight builder executed once at startup. Converts every {@code @RateLimit} annotation on
 * handler method/class to an immutable {@link RateLimitInterceptor}.
 */
public final class RateLimitBotCommandFactory implements BotCommandFactory<RateLimit> {

  @Override
  public @NonNull Class<RateLimit> annotationType() {
    return RateLimit.class;
  }

  @Override
  public void apply(
      @NonNull BotCommand<?> command, @NonNull Method method, @Nullable RateLimit _unused) {

    List<RateLimit> anns = new ArrayList<>();
    anns.addAll(Arrays.asList(method.getAnnotationsByType(RateLimit.class)));
    anns.addAll(Arrays.asList(method.getDeclaringClass().getAnnotationsByType(RateLimit.class)));

    if (anns.isEmpty()) {
      return;
    }

    // 2. Pre-compute stable string prefix “cmd:<method>:”
    String prefix = "cmd:" + method.getName() + ":";

    // 3. Map to immutable meta objects
    List<RateLimitInterceptor.Meta> metas =
        anns.stream()
            .map(a -> new RateLimitInterceptor.Meta(a.key(), a.permits(), a.seconds(), prefix))
            .toList();

    // 4. Create lightweight interceptor
    command.addInterceptor(
        new RateLimitInterceptor(BotSecurityGlobalConfig.INSTANCE.rateLimit().getBackend(), metas));
  }
}
