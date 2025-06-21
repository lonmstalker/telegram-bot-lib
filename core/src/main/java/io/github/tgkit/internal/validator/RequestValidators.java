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
package io.github.tgkit.internal.validator;

import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.BotRequestType;
import io.github.tgkit.internal.exception.ValidationException;
import io.github.tgkit.internal.i18n.MessageKey;

/** Утилиты для валидации свойств {@link BotRequest}. */
public final class RequestValidators {
  private RequestValidators() {}

  /**
   * Валидатор, проверяющий, что тип запроса совпадает с ожидаемым.
   *
   * @param expected ожидаемый тип запроса
   * @return {@link Validator} на {@link BotRequest}, бросающий {@link ValidationException} с ключом
   *     "error.invalidRequestType" при несовпадении
   */
  public static Validator<BotRequest<?>> requestType(BotRequestType expected) {
    return req -> {
      if (req.requestType() != expected) {
        throw new ValidationException(
            new MessageKey("error.invalidRequestType", expected.name(), req.requestType().name()));
      }
    };
  }

  /**
   * Валидатор, проверяющий наличие userId, если {@link BotRequestType#requiresUserId()}.
   *
   * @return {@link Validator} на {@link BotRequest}, бросающий {@link ValidationException} с ключом
   *     "error.missingUserId" при отсутствии userId
   */
  public static Validator<BotRequest<?>> requiresUserId() {
    return req -> {
      BotRequestType type = req.requestType();
      if (type.requiresUserId() && req.user().userId() == null) {
        throw new ValidationException(new MessageKey("error.missingUserId"));
      }
    };
  }

  /**
   * Валидатор, проверяющий наличие chatId, если {@link BotRequestType#requiresChatId()}.
   *
   * @return {@link Validator} на {@link BotRequest}, бросающий {@link ValidationException} с ключом
   *     "error.missingChatId" при отсутствии chatId
   */
  public static Validator<BotRequest<?>> requiresChatId() {
    return req -> {
      BotRequestType type = req.requestType();
      if (type.requiresChatId() && req.user().chatId() == null) {
        throw new ValidationException(new MessageKey("error.missingChatId"));
      }
    };
  }
}
