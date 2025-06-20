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
package io.lonmstalker.tgkit.core.exception;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Простейший {@link BotExceptionHandler}: логирует ошибку и отправляет стандартный ответ.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * BotExceptionHandler handler = BotExceptionHandlerDefault.INSTANCE;
 * }</pre>
 */
public class BotExceptionHandlerDefault implements BotExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(BotExceptionHandlerDefault.class);
  public static final BotExceptionHandler INSTANCE = new BotExceptionHandlerDefault();

  @Override
  public @Nullable BotApiMethod<?> handle(@NonNull Update update, @NonNull Exception ex) {
    log.error("onUpdate with error: ", ex);

    Long chatId = UpdateUtils.resolveChatId(update);
    if (chatId == null) {
      return null;
    }

    return SendMessage.builder()
        .chatId(chatId)
        .text("error.internal")
        .disableNotification(true)
        .build();
  }
}
