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
package io.github.tgkit.testkit;

import io.github.tgkit.api.BotAdapter;
import io.github.tgkit.internal.bot.TelegramSender;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/** Утилита для инъекции тестовых {@link Update} в {@link BotAdapter}. */
public final class UpdateInjector {

  private final BotAdapter adapter;
  private final TelegramSender sender;
  private final AtomicInteger nextId = new AtomicInteger();

  public UpdateInjector(@NonNull BotAdapter adapter, @NonNull TelegramSender sender) {
    this.adapter = adapter;
    this.sender = sender;
  }

  /** Создаёт Update с текстовым сообщением. */
  public Builder text(String text) {
    Message msg = new Message();
    msg.setText(text);
    Update update = new Update();
    update.setMessage(msg);
    return new Builder(update);
  }

  /** Билдер для указания параметров Update. */
  public final class Builder {
    private final Update update;

    private Builder(Update update) {
      this.update = update;
    }

    /** Устанавливает отправителя и чат. */
    public Builder from(long id) {
      Chat chat = new Chat();
      chat.setId(id);
      User user = new User();
      user.setId(id);
      Objects.requireNonNull(update.getMessage()).setChat(chat);
      update.getMessage().setFrom(user);
      return this;
    }

    /** Отправляет Update в бота. */
    public void dispatch() {
      update.setUpdateId(nextId.incrementAndGet());
      BotApiMethod<?> method = adapter.handle(update);
      if (method != null) {
        sender.execute(method);
      }
    }
  }
}
