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

package io.github.tgkit.core.storage;

import io.github.tgkit.core.bot.Bot;
import io.github.tgkit.core.bot.TelegramSender;
import io.github.tgkit.core.exception.BotApiException;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class BotRequestContextHolder {
  private static final ThreadLocal<@Nullable Update> UPDATE = new ThreadLocal<>();
  private static final ThreadLocal<@Nullable Bot> CURRENT_BOT = new ThreadLocal<>();
  private static final ThreadLocal<@Nullable String> REQUEST_ID = new ThreadLocal<>();
  private static final ThreadLocal<@Nullable TelegramSender> SENDER = new ThreadLocal<>();
  private BotRequestContextHolder() {
  }

  public static void init(
      @NonNull Update update, @NonNull Bot currentBot, @NonNull TelegramSender sender) {
    setUpdate(update);
    setSender(sender);
    setBot(currentBot);
    setRequestId(String.valueOf(update.getUpdateId()));
  }

  public static void clear() {
    UPDATE.remove();
    SENDER.remove();
    REQUEST_ID.remove();
    CURRENT_BOT.remove();
  }

  public static @Nullable Update getUpdate() {
    return UPDATE.get();
  }

  public static void setUpdate(@NonNull Update update) {
    UPDATE.set(update);
  }

  public static @Nullable TelegramSender getSender() {
    return SENDER.get();
  }

  public static void setSender(@NonNull TelegramSender sender) {
    SENDER.set(sender);
  }

  public static @Nullable String getRequestId() {
    return REQUEST_ID.get();
  }

  public static void setRequestId(@NonNull String requestId) {
    REQUEST_ID.set(requestId);
  }

  public static @Nullable Bot getBot() {
    return CURRENT_BOT.get();
  }

  public static void setBot(@NonNull Bot bot) {
    CURRENT_BOT.set(bot);
  }

  public static @NonNull Update getUpdateNotNull() {
    return Optional.ofNullable(UPDATE.get())
        .orElseThrow(() -> new BotApiException("Update not set"));
  }

  public static @NonNull TelegramSender getSenderNotNull() {
    return Optional.ofNullable(SENDER.get())
        .orElseThrow(() -> new BotApiException("Sender not set"));
  }

  public static @NonNull String getRequestIdNotNull() {
    return Optional.ofNullable(REQUEST_ID.get())
        .orElseThrow(() -> new BotApiException("Request id not set"));
  }

  public static @NonNull Bot getBotNotNull() {
    return Optional.ofNullable(CURRENT_BOT.get())
        .orElseThrow(() -> new BotApiException("Bot not set"));
  }
}
