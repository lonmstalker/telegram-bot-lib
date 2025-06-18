package io.lonmstalker.tgkit.core.exception;

public class BotApiException extends RuntimeException {

    public BotApiException(String message) {
        super(message);
    }

    public BotApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public BotApiException(Throwable cause) {
        super(cause);
    }
}
