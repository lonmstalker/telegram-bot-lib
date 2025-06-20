package io.lonmstalker.tgkit.core.interceptor;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Стандартный интерцептор, логирующий этапы обработки обновления. */
@Slf4j
public class LoggingBotInterceptor implements BotInterceptor {

  @Override
  public void preHandle(@NonNull Update update, @NonNull BotRequest<?> request) {
    log.debug("Pre handle update: {}", update);
  }

  @Override
  public void postHandle(@NonNull Update update, @NonNull BotRequest<?> request) {
    log.debug("Post handle update: {}", update);
  }

  @Override
  @SuppressWarnings("argument")
  public void afterCompletion(
      @NonNull Update update,
      @Nullable BotRequest<?> request,
      @Nullable BotResponse response,
      @Nullable Exception ex) {
    log.debug("After completion update: {}, response: {}, error: ", update, response, ex);
  }
}
