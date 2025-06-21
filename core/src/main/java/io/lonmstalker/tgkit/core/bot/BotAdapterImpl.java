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

package io.github.tgkit.core.bot;

import io.github.tgkit.core.Bot;
import io.github.tgkit.core.BotAdapter;
import io.github.tgkit.core.BotConfig;
import io.github.tgkit.core.BotInfo;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestConverter;
import io.github.tgkit.core.BotRequestConverterImpl;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.BotService;
import io.github.tgkit.core.args.RouteContextHolder;
import io.github.tgkit.core.i18n.MessageLocalizer;
import io.github.tgkit.core.i18n.NoopMessageLocalizer;
import io.github.tgkit.core.interceptor.BotInterceptor;
import io.github.tgkit.core.state.InMemoryStateStore;
import io.github.tgkit.core.storage.BotRequestContextHolder;
import io.github.tgkit.core.update.UpdateUtils;
import io.github.tgkit.core.user.BotUserInfo;
import io.github.tgkit.core.user.BotUserProvider;
import io.github.tgkit.core.user.SimpleUserProvider;
import io.github.tgkit.core.user.store.InMemoryUserKVStore;
import io.github.tgkit.core.user.store.UserKVStore;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.tuple.Pair;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Адаптер входящих Update → BotRequest → BotCommand → BotResponse. Поддерживает DOS-защиту через
 * {@link RateLimitInterceptor} из security. Для web-hook запросов вызывается синхронно, при
 * long-polling — в пуле.
 */
public class BotAdapterImpl implements BotAdapter, AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(BotAdapterImpl.class);
  private static final BotRequestConverter<BotApiObject> DEFAULT_CONVERTER =
      new BotRequestConverterImpl();

  private final @NonNull BotInfo botInfo;
  private final @NonNull BotConfig config;
  private final @NonNull BotService service;
  private final @NonNull BotCommandRegistry registry;
  private final @NonNull BotUserProvider userProvider;
  private final @NonNull BotRequestConverter<BotApiObject> converter;
  private final @NonNull List<BotInterceptor> interceptors = new CopyOnWriteArrayList<>();

  private @Nullable Bot currentBot;

  private @Nullable TelegramSender sender;

  public BotAdapterImpl(
      long internalId,
      @Nullable BotConfig config,
      @NonNull TelegramSender sender,
      @Nullable UserKVStore userKVStore,
      @Nullable BotUserProvider userProvider,
      @Nullable BotCommandRegistry registry,
      @Nullable List<BotInterceptor> interceptors,
      @Nullable MessageLocalizer messageLocalizer) {
    if (interceptors != null) {
      this.interceptors.addAll(interceptors);
    }
    this.sender = sender;
    this.config = config != null ? config : BotConfig.builder().build();
    this.registry = registry != null ? registry : new BotCommandRegistryImpl();
    this.userProvider = userProvider != null ? userProvider : new SimpleUserProvider();
    this.converter = DEFAULT_CONVERTER;
    this.botInfo = new BotInfo(internalId);
    this.service =
        new BotService(
            this.config.getStore() != null ? this.config.getStore() : new InMemoryStateStore(),
            sender,
            userKVStore == null ? new InMemoryUserKVStore() : userKVStore,
            messageLocalizer != null ? messageLocalizer : new NoopMessageLocalizer());
  }

  static Builder builder() {
    return new Builder();
  }

  void setCurrentBot(@Nullable Bot bot) {
    this.currentBot = bot;
  }

  void setSender(@Nullable TelegramSender sender) {
    this.sender = sender;
  }

  @Override
  public BotApiMethod<?> handle(@NonNull Update update) throws Exception {
    checkStarted();

    BotApiMethod<?> reply;
    Exception error = null;
    Pair<BotResponse, BotRequest<?>> result = null;

    try {
      BotRequestContextHolder.init(
          update, Objects.requireNonNull(currentBot), Objects.requireNonNull(sender));

      result = doHandle(update);
      reply = result != null ? result.getLeft().getMethod() : null;
    } catch (Exception ex) {
      error = ex;
      throw ex;
    } finally {
      Exception afterCompletionError = null;
      for (BotInterceptor interceptor : interceptors) {
        try {
          if (result != null) {
            interceptor.afterCompletion(update, result.getValue(), result.getKey(), error);
          } else {
            interceptor.afterCompletion(update, null, null, error);
          }
        } catch (Exception ex) {
          if (error != null) {
            error.addSuppressed(ex);
          } else if (afterCompletionError == null) {
            afterCompletionError = ex;
          } else {
            afterCompletionError.addSuppressed(ex);
          }
        }
      }
      BotRequestContextHolder.clear();
      RouteContextHolder.clear();
      service.localizer().resetLocale();
      if (error == null && afterCompletionError != null) {
        throw afterCompletionError;
      }
    }

    return reply;
  }

  private @Nullable Pair<BotResponse, BotRequest<?>> doHandle(@NonNull Update update) {
    BotRequestType type = UpdateUtils.getType(update);
    BotApiObject data = converter.convert(update, type);

    BotUserInfo user = userProvider.resolve(update);
    Locale locale = resolveLocale(update, user);

    service.localizer().setLocale(locale);
    BotRequest<BotApiObject> request =
        new BotRequest<>(
            update.getUpdateId(),
            data,
            locale,
            UpdateUtils.resolveMessageId(update),
            botInfo,
            user,
            service,
            type);

    interceptors.forEach(i -> i.preHandle(update, request));
    BotCommand<BotApiObject> command = registry.find(type, config.getBotGroup(), data);
    if (command == null) {
      return null;
    }

    var result = command.handle(request);
    interceptors.forEach(i -> i.postHandle(update, request));

    return Pair.of(result, request);
  }

  private @NonNull Locale resolveLocale(@NonNull Update update, @NonNull BotUserInfo user) {
    Locale locale = user.locale();
    if (locale != null) {
      return locale;
    }
    User tgUser = null;
    try {
      tgUser = UpdateUtils.getUser(update);
    } catch (Exception ignored) {
      // no telegram user
    }
    if (tgUser != null && tgUser.getLanguageCode() != null && !tgUser.getLanguageCode().isBlank()) {
      return Locale.forLanguageTag(tgUser.getLanguageCode());
    }
    return config.getLocale();
  }

  @Override
  public void close() throws IOException {
    try {
      Objects.requireNonNull(sender).close();
    } catch (Exception e) {
      log.warn("Error closing sender", e);
    }
  }

  private void checkStarted() {
    if (currentBot == null || currentBot.state() != BotState.RUNNING) {
      throw new IllegalStateException("Bot adapter not started");
    }
  }

  static class Builder {
    private long internalId;
    private BotConfig config;
    private TelegramSender sender;
    private UserKVStore userKVStore;
    private BotUserProvider userProvider;
    private BotCommandRegistry registry;
    private List<BotInterceptor> interceptors;
    private MessageLocalizer messageLocalizer;

    Builder internalId(long internalId) {
      this.internalId = internalId;
      return this;
    }

    Builder config(@Nullable BotConfig config) {
      this.config = config;
      return this;
    }

    Builder sender(@NonNull TelegramSender sender) {
      this.sender = sender;
      return this;
    }

    Builder userKVStore(@Nullable UserKVStore userKVStore) {
      this.userKVStore = userKVStore;
      return this;
    }

    Builder userProvider(@Nullable BotUserProvider userProvider) {
      this.userProvider = userProvider;
      return this;
    }

    Builder registry(@Nullable BotCommandRegistry registry) {
      this.registry = registry;
      return this;
    }

    Builder interceptors(@Nullable List<BotInterceptor> interceptors) {
      this.interceptors = interceptors;
      return this;
    }

    Builder messageLocalizer(@Nullable MessageLocalizer localizer) {
      this.messageLocalizer = localizer;
      return this;
    }

    BotAdapterImpl build() {
      return new BotAdapterImpl(
          internalId,
          config,
          sender,
          userKVStore,
          userProvider,
          registry,
          interceptors,
          messageLocalizer);
    }
  }
}
