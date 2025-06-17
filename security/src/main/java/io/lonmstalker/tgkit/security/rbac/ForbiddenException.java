package io.lonmstalker.tgkit.security.rbac;

import io.lonmstalker.tgkit.core.exception.BotApiException;

/** Отсутствуют необходимые права для выполнения команды. */
public final class ForbiddenException extends BotApiException {
    public ForbiddenException(String msg) { super(msg); }
}