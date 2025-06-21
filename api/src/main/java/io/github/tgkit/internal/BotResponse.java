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
package io.github.tgkit.internal;

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

  public static Builder builder() {
    return new Builder();
  }

  public @Nullable BotApiMethod<?> getMethod() {
    return method;
  }

  public void setMethod(@Nullable BotApiMethod<?> method) {
    this.method = method;
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
