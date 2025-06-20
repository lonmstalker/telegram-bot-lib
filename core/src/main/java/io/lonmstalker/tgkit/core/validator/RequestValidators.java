package io.lonmstalker.tgkit.core.validator;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.ValidationException;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Утилиты для валидации свойств {@link BotRequest}. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RequestValidators {

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
