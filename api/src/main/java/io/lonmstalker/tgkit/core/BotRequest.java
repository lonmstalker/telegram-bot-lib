/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.dsl.*;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Обёртка над обновлением Telegram, содержащая дополнительную информацию о боте и пользователе.
 *
 * @param updateId идентификатор обновления
 * @param data данные обновления
 * @param botInfo сведения о боте
 * @param user информация о пользователе
 * @param requestType Тип запроса
 * @param <T> тип данных обновления
 */
public record BotRequest<T>(
    int updateId,
    @NonNull T data,
    @NonNull Locale locale,
    @Nullable Integer msgId,
    @NonNull BotInfo botInfo,
    @NonNull BotUserInfo user,
    @NonNull BotService service,
    @NonNull BotRequestType requestType) {

  public void requiredChatId() {
    if (user.chatId() == null) {
      throw new BotApiException("Missing required chat id");
    }
  }

  public void requiredUserId() {
    if (user.userId() == null) {
      throw new BotApiException("Missing required user id");
    }
  }

  /** Сообщение. */
  public @NonNull MessageBuilder msg(@NonNull String text) {
    return BotDSL.msg(BotDSL.ctx(botInfo(), user(), service()), text);
  }

  /** Сообщение из i18n. */
  public @NonNull MessageBuilder msgKey(@NonNull String key, @NonNull Object... args) {
    return BotDSL.msgKey(BotDSL.ctx(botInfo(), user(), service()), key, args);
  }

  /** Фото. */
  public @NonNull PhotoBuilder photo(@NonNull InputFile file) {
    return BotDSL.photo(BotDSL.ctx(botInfo(), user(), service()), file);
  }

  /** Редактирование сообщения. */
  public @NonNull EditBuilder edit(long msgId) {
    return BotDSL.edit(BotDSL.ctx(botInfo(), user(), service()), msgId);
  }

  /** Удаление сообщения. */
  public @NonNull DeleteBuilder delete(long msgId) {
    return BotDSL.delete(BotDSL.ctx(botInfo(), user(), service()), msgId);
  }

  /** Отправка медиа-группы. */
  public @NonNull MediaGroupBuilder mediaGroup() {
    return BotDSL.mediaGroup(BotDSL.ctx(botInfo(), user(), service()));
  }

  /** Опрос. */
  public @NonNull PollBuilder poll(@NonNull String question) {
    return BotDSL.poll(BotDSL.ctx(botInfo(), user(), service()), question);
  }

  /** Викторина. */
  public @NonNull QuizBuilder quiz(@NonNull String question, int correct) {
    return BotDSL.quiz(BotDSL.ctx(botInfo(), user(), service()), question, correct);
  }

  /** Результаты инлайн-запроса. */
  public @NonNull InlineResultBuilder inline() {
    return BotDSL.inline(BotDSL.ctx(botInfo(), user(), service()));
  }
}
