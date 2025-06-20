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

import io.github.tgkit.api.BotRequest;
import io.github.tgkit.api.BotResponse;
import io.github.tgkit.api.interceptor.BotInterceptor;
import io.github.tgkit.api.update.UpdateUtils;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Runtime-cheap rate-limit guard. Heavy computations delegated to {@link
 * RateLimitBotCommandFactory}.
 */
public final class RateLimitInterceptor implements BotInterceptor {

  private final RateLimiter backend;
  private final List<Meta> metas;

  RateLimitInterceptor(RateLimiter backend, List<Meta> metas) {
    this.backend = backend;
    this.metas = metas;
  }

  @Override
  /** Проверяет превышение лимитов и бросает исключение при необходимости. */
  public void preHandle(@NonNull Update upd, @NonNull BotRequest<?> request) {
    Long uid = UpdateUtils.resolveUserId(upd);
    Long chat = UpdateUtils.resolveChatId(upd);

    for (Meta m : metas) {
      String k =
          switch (m.key) {
            case USER -> m.prefix + "user:" + uid;
            case CHAT -> m.prefix + "chat:" + chat;
            case GLOBAL -> m.prefix + "global";
          };
      if (!backend.tryAcquire(k, m.permits, m.seconds)) {
        throw new RateLimitExceededException();
      }
    }
  }

  @Override
  public void postHandle(@NonNull Update u, @NonNull BotRequest<?> request) {
    /* noop */
  }

  @Override
  public void afterCompletion(
      @NonNull Update u,
      @Nullable BotRequest<?> req,
      @Nullable BotResponse r,
      @Nullable Exception e) {
    /* noop */
  }

  /** immutable meta per annotation */
  record Meta(LimiterKey key, int permits, int seconds, String prefix) {}

  public static final class RateLimitExceededException extends RuntimeException {
    RateLimitExceededException() {
      super("rate limit exceeded");
    }
  }
}
