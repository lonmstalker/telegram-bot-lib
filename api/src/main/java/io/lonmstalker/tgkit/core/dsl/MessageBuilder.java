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
package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.validator.TextLengthValidator;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.parse_mode.Sanitizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/** Создание текстового сообщения. */
@SuppressWarnings("initialization.fields.uninitialized")
public final class MessageBuilder extends BotDSL.CommonBuilder<MessageBuilder, SendMessage> {
  private static final TextLengthValidator VALIDATOR = new TextLengthValidator();
  private final String text;
  private ParseMode parseMode;
  private Boolean sanitize;

  MessageBuilder(DSLContext ctx, String text) {
    super(ctx);
    this.text = text;
  }

  public @NonNull MessageBuilder parseMode(@NonNull ParseMode mode) {
    this.parseMode = mode;
    return this;
  }

  public @NonNull MessageBuilder sanitize(boolean sanitize) {
    this.sanitize = sanitize;
    return this;
  }

  @Override
  public @NonNull SendMessage build() {
    requireChatId();

    ParseMode p = parseMode != null ? parseMode : BotGlobalConfig.INSTANCE.dsl().getParseMode();
    boolean s = this.sanitize != null ? this.sanitize : BotGlobalConfig.INSTANCE.dsl().isSanitize();

    String t = s ? Sanitizer.sanitize(text, p) : text;

    VALIDATOR.validate(text);

    SendMessage msg = new SendMessage(String.valueOf(chatId), t);
    msg.setParseMode(p.getMode());
    msg.setDisableNotification(disableNotif);

    if (replyTo != null) {
      msg.setReplyToMessageId(replyTo.intValue());
    }
    if (keyboard != null) {
      msg.setReplyMarkup(keyboard.build());
    }

    return msg;
  }
}
