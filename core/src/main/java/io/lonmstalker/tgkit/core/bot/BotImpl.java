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

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.event.impl.StartStatusBotEvent;
import io.lonmstalker.tgkit.core.event.impl.StopStatusBotEvent;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.User;

@SuppressWarnings({"dereference.of.nullable", "argument"})
public final class BotImpl implements Bot {
  private static final Logger log = LoggerFactory.getLogger(BotImpl.class);

  private BotImpl(
      long id,
      @Nullable User user,
      @Nullable SetWebhook setWebhook,
      @NonNull String token,
      @NonNull BotConfig config,
      @NonNull DefaultAbsSender absSender,
      @NonNull BotCommandRegistry commandRegistry,
      @Nullable BotSessionImpl session,
      long onCompletedActionTimeoutMs) {
    this.id = id;
    this.user = user;
    this.setWebhook = setWebhook;
    this.token = token;
    this.config = config;
    this.absSender = absSender;
    this.commandRegistry = commandRegistry;
    this.session = session;
    this.onCompletedActionTimeoutMs = onCompletedActionTimeoutMs;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private long id;
    private User user;
    private SetWebhook setWebhook;
    private String token;
    private BotConfig config;
    private DefaultAbsSender absSender;
    private BotCommandRegistry commandRegistry;
    private BotSessionImpl session;
    private long onCompletedActionTimeoutMs = 10_000;

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder user(@Nullable User user) {
      this.user = user;
      return this;
    }

    public Builder setWebhook(@Nullable SetWebhook setWebhook) {
      this.setWebhook = setWebhook;
      return this;
    }

    public Builder token(@NonNull String token) {
      this.token = token;
      return this;
    }

    public Builder config(@NonNull BotConfig config) {
      this.config = config;
      return this;
    }

    public Builder absSender(@NonNull DefaultAbsSender absSender) {
      this.absSender = absSender;
      return this;
    }

    public Builder commandRegistry(@NonNull BotCommandRegistry registry) {
      this.commandRegistry = registry;
      return this;
    }

    public Builder session(@Nullable BotSessionImpl session) {
      this.session = session;
      return this;
    }

    public Builder onCompletedActionTimeoutMs(long timeout) {
      this.onCompletedActionTimeoutMs = timeout;
      return this;
    }

    public BotImpl build() {
      return new BotImpl(
          id,
          user,
          setWebhook,
          token,
          config,
          absSender,
          commandRegistry,
          session,
          onCompletedActionTimeoutMs);
    }
  }

  private final AtomicReference<BotState> state = new AtomicReference<>(BotState.NEW);
  private final @NonNull List<BotCompleteAction> completeActions = new CopyOnWriteArrayList<>();
  private long id;
  private volatile @Nullable User user;
  private @Nullable SetWebhook setWebhook;
  private @NonNull String token;
  private @NonNull BotConfig config;
  private @NonNull DefaultAbsSender absSender;
  private @NonNull BotCommandRegistry commandRegistry;
  private @Nullable BotSessionImpl session;

  private long onCompletedActionTimeoutMs = 10_000;

  public AtomicReference<BotState> getState() {
    return state;
  }

  public List<BotCompleteAction> getCompleteActions() {
    return completeActions;
  }

  public long getId() {
    return id;
  }

  public @Nullable User getUser() {
    return user;
  }

  public @Nullable SetWebhook getSetWebhook() {
    return setWebhook;
  }

  public @NonNull String getToken() {
    return token;
  }

  public @NonNull BotConfig getConfig() {
    return config;
  }

  public @NonNull DefaultAbsSender getAbsSender() {
    return absSender;
  }

  public @NonNull BotCommandRegistry getCommandRegistry() {
    return commandRegistry;
  }

  public @Nullable BotSessionImpl getSession() {
    return session;
  }

  public long getOnCompletedActionTimeoutMs() {
    return onCompletedActionTimeoutMs;
  }

  @Override
  public long internalId() {
    checkStarted();
    return id;
  }

  @Override
  @SuppressWarnings("argument")
  public long externalId() {
    checkStarted();
    return Objects.requireNonNull(user).getId();
  }

  @Override
  @SuppressWarnings("dereference.of.nullable")
  public void start() {
    checkNotStarted();
    try {
      if (absSender instanceof LongPollingReceiver receiver) {
        initLongPolling(receiver);
      } else if (absSender instanceof WebHookReceiver receiver) {
        initWebHook(receiver);
      }
      BotRegistryImpl.getInstance().register(this);
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new StartStatusBotEvent(internalId(), externalId(), Instant.now(), null));
      state.set(BotState.RUNNING);
    } catch (Throwable ex) {
      BotRegistryImpl.getInstance().unregister(this);
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new StartStatusBotEvent(internalId(), externalId(), Instant.now(), ex));
      throw new BotApiException("Error starting bot", ex);
    }
  }

  @Override
  @SuppressWarnings("dereference.of.nullable")
  public void stop() {
    try {
      checkStarted();
      runCompleteActions();
      shutdownSession();
      closeAbsSender();
      clearState();
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new StopStatusBotEvent(internalId(), externalId(), Instant.now(), null));
    } catch (Throwable ex) {
      BotGlobalConfig.INSTANCE
          .events()
          .getBus()
          .publish(new StopStatusBotEvent(internalId(), externalId(), Instant.now(), ex));
      throw new BotApiException("Error stopping bot", ex);
    } finally {
      state.set(BotState.STOPPED);
    }
  }

  @Override
  @SuppressWarnings("argument")
  public @NonNull String username() {
    checkStarted();
    return Objects.requireNonNull(user).getUserName();
  }

  @Override
  public @NonNull BotCommandRegistry registry() {
    return commandRegistry;
  }

  @Override
  public @NonNull BotState state() {
    return state.get();
  }

  @Override
  public void onComplete(@NonNull BotCompleteAction action) {
    completeActions.add(action);
  }

  @Override
  public @NonNull BotConfig config() {
    return this.config;
  }

  @Override
  public @NonNull String token() {
    return this.token;
  }

  @Override
  public @NonNull BotRegistry botRegistry() {
    return BotRegistryImpl.getInstance();
  }

  private void initLongPolling(@NonNull LongPollingReceiver receiver) throws Exception {
    receiver.clearWebhook();
    if (this.session == null) {
      this.session = new BotSessionImpl();
    }
    this.session.setOptions(config);
    this.session.setToken(token);
    this.session.setCallback(receiver);
    this.session.start();
    this.user = absSender.execute(new GetMe());
    receiver.setUsername(Objects.requireNonNull(this.user).getUserName());
  }

  private void initWebHook(WebHookReceiver receiver) throws Exception {
    if (this.setWebhook != null) {
      setWebhook.validate();
      receiver.setWebhook(setWebhook);
    }
    this.user = absSender.execute(new GetMe());
    receiver.setUsername(Objects.requireNonNull(this.user).getUserName());
  }

  private void runCompleteActions() {
    try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
      for (BotCompleteAction action : completeActions) {
        try {
          executor
              .submit(
                  () -> {
                    try {
                      action.complete();
                    } catch (Exception e) {
                      throw new RuntimeException(e);
                    }
                  })
              .get(onCompletedActionTimeoutMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
          log.error("Complete action error", e);
        }
      }
    }
  }

  private void shutdownSession() {
    if (session != null && session.isRunning()) {
      try {
        session.stop();
      } catch (Exception e) {
        log.warn("Error stopping session", e);
      }
    }
  }

  private void closeAbsSender() {
    if (absSender instanceof AutoCloseable closeable) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.warn("Error closing sender", e);
      }
    }
  }

  private void clearState() {
    this.user = null;
    this.setWebhook = null;
    this.session = null;
    BotRegistryImpl.getInstance().unregister(this);
  }

  private void checkStarted() {
    if (user == null) {
      throw new BotApiException("Bot not started");
    }
  }

  private void checkNotStarted() {
    if (user != null) {
      throw new BotApiException("Bot started");
    }
  }
}
