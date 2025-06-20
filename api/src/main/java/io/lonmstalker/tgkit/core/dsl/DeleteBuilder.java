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

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

/** Удаление сообщения. */
public final class DeleteBuilder extends BotDSL.CommonBuilder<DeleteBuilder, DeleteMessage> {
  private final long msgId;

  DeleteBuilder(@NonNull DSLContext ctx, long msgId) {
    super(ctx);
    this.msgId = msgId;
  }

  @Override
  public @NonNull DeleteMessage build() {
    return new DeleteMessage(String.valueOf(chatId), (int) msgId);
  }
}
