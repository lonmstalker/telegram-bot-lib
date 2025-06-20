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
package io.lonmstalker.tgkit.core.bot;

import static io.lonmstalker.tgkit.core.bot.BotConstants.BOT_TOKEN_SECRET;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandlerDefault;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class LongPollingReceiver extends TelegramLongPollingBot implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(LongPollingReceiver.class);
  private final @NonNull BotAdapter adapter;
  private final @NonNull TelegramSender sender;
  private final @NonNull BotExceptionHandler globalExceptionHandler;

  private @Nullable String username;

  void setUsername(@Nullable String username) {
    this.username = username;
  }

  public LongPollingReceiver(
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

  public LongPollingReceiver(
      @NonNull BotConfig options,
      @NonNull BotAdapter adapter,
      @NonNull String token,
      @NonNull TelegramSender sender,
      @Nullable BotExceptionHandler globalExceptionHandler) {
    super(options, token);
    this.adapter = adapter;
    this.sender = sender;
    this.globalExceptionHandler =
        globalExceptionHandler != null
            ? globalExceptionHandler
            : BotExceptionHandlerDefault.INSTANCE;
    if (adapter instanceof BotAdapterImpl b) {
      b.setSender(sender);
    }
  }

  @Override
  public void onUpdateReceived(@NonNull Update update) {
    try {
      var result = adapter.handle(update);
      if (result != null) {
        execute(result);
      }
    } catch (Exception e) {
      var response = globalExceptionHandler.handle(update, e);
      if (response != null) {
        try {
          execute(response);
        } catch (TelegramApiException ex) {
          log.error("globalExceptionHandler response failed", ex);
        }
      }
    }
  }

  @Override
  public @NonNull String getBotUsername() {
    return username != null ? username : StringUtils.EMPTY;
  }

  @Override
  public void close() throws Exception {
    onClosing();
    if (adapter instanceof AutoCloseable) {
      try {
        ((AutoCloseable) adapter).close();
      } catch (Exception e) {
        log.warn("Error closing adapter", e);
      }
    }
  }
}
