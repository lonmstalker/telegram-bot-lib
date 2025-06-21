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
package io.github.tgkit.core.interceptor;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Стандартный интерцептор, логирующий этапы обработки обновления. */
public class LoggingBotInterceptor implements BotInterceptor {
  private static final Logger log = LoggerFactory.getLogger(LoggingBotInterceptor.class);

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
