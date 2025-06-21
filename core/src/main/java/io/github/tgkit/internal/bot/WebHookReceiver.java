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
package io.github.tgkit.internal.bot;

import static io.github.tgkit.internal.bot.BotConstants.BOT_TOKEN_SECRET;

import io.github.tgkit.internal.BotAdapter;
import io.github.tgkit.internal.exception.BotExceptionHandler;
import io.github.tgkit.internal.exception.BotExceptionHandlerDefault;
import io.github.tgkit.security.secret.SecretStore;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

class WebHookReceiver extends TelegramWebhookBot implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WebHookReceiver.class);
  private final @NonNull String token;
  private final @NonNull BotAdapter adapter;
  private final @NonNull TelegramSender sender;
  private final @NonNull BotExceptionHandler globalExceptionHandler;

  private @Nullable String username;

  public WebHookReceiver(
      @NonNull BotConfig options,
      @NonNull BotAdapter adapter,
      @NonNull SecretStore store,
      @NonNull TelegramSender sender,
      @Nullable BotExceptionHandler globalExceptionHandler) {
    this(
        options,
        adapter,
        store
            .get(BOT_TOKEN_SECRET)
            .orElseThrow(() -> new IllegalArgumentException("secret 'bot_token' not found")),
        sender,
        globalExceptionHandler);
  }

  public WebHookReceiver(
      @NonNull BotConfig options,
      @NonNull BotAdapter adapter,
      @NonNull String token,
      @NonNull TelegramSender sender,
      @Nullable BotExceptionHandler globalExceptionHandler) {
    super(options, token);
    this.token = token;
    this.sender = sender;
    this.adapter = adapter;
    this.globalExceptionHandler =
        globalExceptionHandler != null
            ? globalExceptionHandler
            : BotExceptionHandlerDefault.INSTANCE;
    if (adapter instanceof BotAdapterImpl b) {
      b.setSender(sender);
    }
  }

  void setUsername(@Nullable String username) {
    this.username = username;
  }

  @Override
  public @NonNull String getBotUsername() {
    return username != null ? username : StringUtils.EMPTY;
  }

  @Override
  @SuppressWarnings("override.return")
  public @Nullable BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    try {
      return adapter.handle(update);
    } catch (Exception e) {
      return globalExceptionHandler.handle(update, e);
    }
  }

  @Override
  public @NonNull String getBotPath() {
    return token;
  }

  @Override
  public void close() {
    if (adapter instanceof AutoCloseable) {
      try {
        ((AutoCloseable) adapter).close();
      } catch (Exception e) {
        log.warn("Error closing adapter", e);
      }
    }
  }
}
