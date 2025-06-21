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

import io.github.tgkit.core.exception.BotExceptionHandler;
import io.github.tgkit.core.interceptor.BotInterceptor;
import io.github.tgkit.core.state.StateStore;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.generics.BackOff;

/**
 * Конфигурация бота и отправки сообщений через {@link TelegramSender}.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * BotConfig cfg = BotConfig.builder().build();
 * cfg.setRequestsPerSecond(30);
 * }</pre>
 */
public class BotConfig extends DefaultBotOptions {
  public static final int DEFAULT_UPDATE_QUEUE_CAPACITY = 1000;
  public static final long DEFAULT_ON_COMPLETED_ACTION_TIMEOUT_MS = 10_000L;
  private static final String BASE_URL = "https://api.telegram.org/bot";
  private @NonNull Locale locale;
  private @NonNull String botGroup;
  private @Nullable StateStore store;
  private @NonNull List<BotInterceptor> globalInterceptors;
  private @Nullable BotExceptionHandler globalExceptionHandler;
  private int requestsPerSecond;
  private int updateQueueCapacity;
  private long onCompletedActionTimeoutMs;

  @SuppressWarnings({"argument", "method.invocation"})
  private BotConfig(
      @Nullable BotExceptionHandler globalExceptionHandler,
      @Nullable List<BotInterceptor> globalInterceptors,
      @Nullable StateStore store,
      @Nullable String botGroup,
      @Nullable Integer requestsPerSecond,
      @Nullable Locale locale,
      @Nullable String proxyHost,
      @Nullable Integer proxyPort,
      @Nullable ProxyType proxyType,
      @Nullable String baseUrl,
      @Nullable Integer maxThreads,
      @Nullable RequestConfig requestConfig,
      @Nullable HttpContext httpContext,
      @Nullable BackOff backOff,
      @Nullable Integer maxWebhookConnections,
      @Nullable List<String> allowedUpdates,
      @Nullable Integer getUpdatesTimeout,
      @Nullable Integer getUpdatesLimit,
      @Nullable Integer updateQueueCapacity,
      @Nullable Long onCompletedActionTimeoutMs) {
    this.store = store;
    this.globalExceptionHandler = globalExceptionHandler;
    this.globalInterceptors = globalInterceptors == null ? new ArrayList<>() : globalInterceptors;
    this.locale = locale != null ? locale : Locale.getDefault();
    this.botGroup = botGroup != null ? botGroup : "";
    this.requestsPerSecond = requestsPerSecond != null ? requestsPerSecond : 30;
    this.updateQueueCapacity =
        updateQueueCapacity != null ? updateQueueCapacity : DEFAULT_UPDATE_QUEUE_CAPACITY;
    this.onCompletedActionTimeoutMs =
        onCompletedActionTimeoutMs != null
            ? onCompletedActionTimeoutMs
            : DEFAULT_ON_COMPLETED_ACTION_TIMEOUT_MS;

    this.setProxyHost(proxyHost);
    this.setAllowedUpdates(allowedUpdates);
    this.setBaseUrl(baseUrl != null ? baseUrl : BASE_URL);
    this.setProxyPort(proxyPort != null ? proxyPort : 0);
    this.setProxyType(proxyType != null ? proxyType : ProxyType.NO_PROXY);
    this.setMaxThreads(maxThreads != null ? maxThreads : 1);
    this.setHttpContext(httpContext != null ? httpContext : HttpClientContext.create());
    this.setRequestConfig(requestConfig != null ? requestConfig : RequestConfig.DEFAULT);
    this.setBackOff(backOff != null ? backOff : new ExponentialBackOff());
    this.setGetUpdatesTimeout(getUpdatesTimeout != null ? getUpdatesTimeout : 50);
    this.setMaxWebhookConnections(maxWebhookConnections != null ? maxWebhookConnections : 40);
    this.setGetUpdatesLimit(getUpdatesLimit != null ? getUpdatesLimit : 100);
  }

  public static Builder builder() {
    return new Builder();
  }

  public @NonNull Locale getLocale() {
    return locale;
  }

  public void setLocale(@NonNull Locale locale) {
    this.locale = locale;
  }

  public @NonNull String getBotGroup() {
    return botGroup;
  }

  public void setBotGroup(@NonNull String botGroup) {
    this.botGroup = botGroup;
  }

  public @Nullable StateStore getStore() {
    return store;
  }

  public void setStore(@Nullable StateStore store) {
    this.store = store;
  }

  public @NonNull List<BotInterceptor> getGlobalInterceptors() {
    return globalInterceptors;
  }

  public void setGlobalInterceptors(@NonNull List<BotInterceptor> globalInterceptors) {
    this.globalInterceptors = globalInterceptors;
  }

  public @Nullable BotExceptionHandler getGlobalExceptionHandler() {
    return globalExceptionHandler;
  }

  public void setGlobalExceptionHandler(@Nullable BotExceptionHandler globalExceptionHandler) {
    this.globalExceptionHandler = globalExceptionHandler;
  }

  public int getRequestsPerSecond() {
    return requestsPerSecond;
  }

  public void setRequestsPerSecond(int requestsPerSecond) {
    this.requestsPerSecond = requestsPerSecond;
  }

  public int getUpdateQueueCapacity() {
    return updateQueueCapacity;
  }

  public void setUpdateQueueCapacity(int updateQueueCapacity) {
    this.updateQueueCapacity = updateQueueCapacity;
  }

  public long getOnCompletedActionTimeoutMs() {
    return onCompletedActionTimeoutMs;
  }

  public void setOnCompletedActionTimeoutMs(long onCompletedActionTimeoutMs) {
    this.onCompletedActionTimeoutMs = onCompletedActionTimeoutMs;
  }

  public static final class Builder {
    private BotExceptionHandler globalExceptionHandler;
    private List<BotInterceptor> globalInterceptors = new ArrayList<>();
    private StateStore store;
    private String botGroup;
    private Integer requestsPerSecond;
    private Locale locale;
    private String proxyHost;
    private Integer proxyPort;
    private ProxyType proxyType;
    private String baseUrl;
    private Integer maxThreads;
    private RequestConfig requestConfig;
    private HttpContext httpContext;
    private BackOff backOff;
    private Integer maxWebhookConnections;
    private List<String> allowedUpdates;
    private Integer getUpdatesTimeout;
    private Integer getUpdatesLimit;
    private Integer updateQueueCapacity;
    private Long onCompletedActionTimeoutMs;

    public Builder globalExceptionHandler(BotExceptionHandler handler) {
      this.globalExceptionHandler = handler;
      return this;
    }

    public Builder globalInterceptors(List<BotInterceptor> interceptors) {
      this.globalInterceptors = interceptors != null ? interceptors : new ArrayList<>();
      return this;
    }

    public Builder addGlobalInterceptor(BotInterceptor interceptor) {
      this.globalInterceptors.add(interceptor);
      return this;
    }

    public Builder store(StateStore store) {
      this.store = store;
      return this;
    }

    public Builder botGroup(String botGroup) {
      this.botGroup = botGroup;
      return this;
    }

    public Builder requestsPerSecond(Integer requestsPerSecond) {
      this.requestsPerSecond = requestsPerSecond;
      return this;
    }

    public Builder locale(Locale locale) {
      this.locale = locale;
      return this;
    }

    public Builder proxyHost(String proxyHost) {
      this.proxyHost = proxyHost;
      return this;
    }

    public Builder proxyPort(Integer proxyPort) {
      this.proxyPort = proxyPort;
      return this;
    }

    public Builder proxyType(ProxyType proxyType) {
      this.proxyType = proxyType;
      return this;
    }

    public Builder baseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
      return this;
    }

    public Builder maxThreads(Integer maxThreads) {
      this.maxThreads = maxThreads;
      return this;
    }

    public Builder requestConfig(RequestConfig requestConfig) {
      this.requestConfig = requestConfig;
      return this;
    }

    public Builder httpContext(HttpContext httpContext) {
      this.httpContext = httpContext;
      return this;
    }

    public Builder backOff(BackOff backOff) {
      this.backOff = backOff;
      return this;
    }

    public Builder maxWebhookConnections(Integer maxWebhookConnections) {
      this.maxWebhookConnections = maxWebhookConnections;
      return this;
    }

    public Builder allowedUpdates(List<String> allowedUpdates) {
      this.allowedUpdates = allowedUpdates;
      return this;
    }

    public Builder getUpdatesTimeout(Integer getUpdatesTimeout) {
      this.getUpdatesTimeout = getUpdatesTimeout;
      return this;
    }

    public Builder getUpdatesLimit(Integer getUpdatesLimit) {
      this.getUpdatesLimit = getUpdatesLimit;
      return this;
    }

    public Builder updateQueueCapacity(Integer updateQueueCapacity) {
      this.updateQueueCapacity = updateQueueCapacity;
      return this;
    }

    public Builder onCompletedActionTimeoutMs(Long onCompletedActionTimeoutMs) {
      this.onCompletedActionTimeoutMs = onCompletedActionTimeoutMs;
      return this;
    }

    public BotConfig build() {
      return new BotConfig(
          globalExceptionHandler,
          globalInterceptors,
          store,
          botGroup,
          requestsPerSecond,
          locale,
          proxyHost,
          proxyPort,
          proxyType,
          baseUrl,
          maxThreads,
          requestConfig,
          httpContext,
          backOff,
          maxWebhookConnections,
          allowedUpdates,
          getUpdatesTimeout,
          getUpdatesLimit,
          updateQueueCapacity,
          onCompletedActionTimeoutMs);
    }
  }
}
