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
package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.parse_mode.Sanitizer;
import java.time.Duration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

/** Редактирование сообщения. */
@SuppressWarnings("initialization.fields.uninitialized")
public final class EditBuilder extends BotDSL.CommonBuilder<EditBuilder, EditMessageText> {
  private final long msgId;
  private Duration typing;
  private String newText;
  private ParseMode parseMode;
  private Boolean sanitize;

  EditBuilder(@NonNull DSLContext ctx, long msgId) {
    super(ctx);
    this.msgId = msgId;
  }

  /** Показать набор текста перед редактированием. */
  public @NonNull EditBuilder typing(@NonNull Duration d) {
    this.typing = d;
    return this;
  }

  /** Текст после редактирования. */
  public @NonNull EditBuilder thenEdit(@NonNull String text) {
    this.newText = text;
    return this;
  }

  public @NonNull EditBuilder parseMode(@NonNull ParseMode mode) {
    this.parseMode = mode;
    return this;
  }

  public @NonNull EditBuilder sanitize(boolean sanitize) {
    this.sanitize = sanitize;
    return this;
  }

  @Override
  public @NonNull EditMessageText build() {
    requireChatId();

    ParseMode p = parseMode != null ? parseMode : BotGlobalConfig.INSTANCE.dsl().getParseMode();
    boolean s = this.sanitize != null ? this.sanitize : BotGlobalConfig.INSTANCE.dsl().isSanitize();

    String t = s ? Sanitizer.sanitize(newText, p) : newText;

    if (typing != null) {
      SendChatAction act = new SendChatAction();
      act.setChatId(String.valueOf(chatId));
      act.setAction(ActionType.TYPING);
      super.ctx.service().sender().execute(act);
    }

    EditMessageText edit = new EditMessageText();
    edit.setChatId(String.valueOf(chatId));
    edit.setMessageId((int) msgId);
    edit.setText(t);

    return edit;
  }
}
