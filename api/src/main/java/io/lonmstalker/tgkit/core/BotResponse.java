package io.lonmstalker.tgkit.core;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/** Ответ бота в виде метода Telegram API. */
public class BotResponse {

  /** Метод, который будет выполнен Telegram API. */
  private @Nullable BotApiMethod<?> method;

  public BotResponse() {
    this(null);
  }

  public BotResponse(@Nullable BotApiMethod<?> method) {
    this.method = method;
  }

  public @Nullable BotApiMethod<?> getMethod() {
    return method;
  }

  public void setMethod(@Nullable BotApiMethod<?> method) {
    this.method = method;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private BotApiMethod<?> method;

    public Builder method(@Nullable BotApiMethod<?> method) {
      this.method = method;
      return this;
    }

    public BotResponse build() {
      return new BotResponse(method);
    }
  }
}
