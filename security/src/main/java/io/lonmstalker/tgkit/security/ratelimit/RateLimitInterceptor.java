package io.lonmstalker.tgkit.security.ratelimit;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.update.UpdateUtils;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Runtime-cheap rate-limit guard. Heavy computations delegated to {@link
 * RateLimitBotCommandFactory}.
 */
public final class RateLimitInterceptor implements BotInterceptor {

  RateLimitInterceptor(RateLimiter backend, List<Meta> metas) {
    this.backend = backend;
    this.metas = metas;
  }

  /** immutable meta per annotation */
  record Meta(LimiterKey key, int permits, int seconds, String prefix) {}

  private final RateLimiter backend;
  private final List<Meta> metas;

  @Override
  /**
   * Проверяет превышение лимитов и бросает исключение при необходимости.
   */
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

  public static final class RateLimitExceededException extends RuntimeException {
    RateLimitExceededException() {
      super("rate limit exceeded");
    }
  }
}
