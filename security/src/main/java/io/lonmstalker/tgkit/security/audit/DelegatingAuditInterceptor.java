package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DelegatingAuditInterceptor implements BotInterceptor {

  private final @NonNull AuditBus auditBus;
  private final @NonNull AuditConverter conv;

  @Override
  public void preHandle(@NonNull Update u, @NonNull BotRequest<?> request) {
    publish(conv, u);
  }

  @Override
  public void postHandle(@NonNull Update u, @NonNull BotRequest<?> request) {
    /*no-op*/
  }

  @Override
  public void afterCompletion(
      @NonNull Update u,
      @Nullable BotRequest<?> req,
      @Nullable BotResponse r,
      @Nullable Exception ex) {
    /*no-op*/
  }

  private void publish(@NonNull AuditConverter c, @NonNull Update update) {
    auditBus.publish(c.convert(update));
  }
}
