package io.lonmstalker.tgkit.security.antispam;

import io.lonmstalker.tgkit.core.exception.BotApiException;

/** Сигнал “тихо отбросить update и не вызывать другие хендлеры”. */
public final class DropUpdateException extends BotApiException {
  public DropUpdateException(String msg) {
    super(msg);
  }
}
