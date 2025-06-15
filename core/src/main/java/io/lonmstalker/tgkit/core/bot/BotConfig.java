package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.state.StateStore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;
import org.telegram.telegrambots.meta.generics.BackOff;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Setter
@Getter
public class BotConfig extends DefaultBotOptions {
    private static final String BASE_URL = "https://api.telegram.org/bot";
    private @NonNull Locale locale;
    private @NonNull StateStore store;
    private @NonNull String botPattern;
    private @Nullable BotExceptionHandler globalExceptionHandler;
    private @NonNull List<BotInterceptor> globalInterceptors = List.of();
    private int requestsPerSecond;

    public void addInterceptor(@NonNull BotInterceptor interceptor) {
        var list = new ArrayList<>(this.globalInterceptors);
        list.add(interceptor);
        this.globalInterceptors = List.copyOf(list);
    }

    public void addInterceptors(@NonNull List<BotInterceptor> interceptors) {
        var list = new ArrayList<>(this.globalInterceptors);
        list.addAll(interceptors);
        this.globalInterceptors = List.copyOf(list);
    }

    @Builder
    @SuppressWarnings({"argument", "method.invocation"})
    public BotConfig(@Nullable BotExceptionHandler globalExceptionHandler,
                     List<BotInterceptor> globalInterceptors,
                     @Nullable StateStore store,
                     @Nullable String botPattern,
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
                     @Nullable Integer getUpdatesLimit) {
        this.globalExceptionHandler = globalExceptionHandler;
        if (globalInterceptors != null) {
            this.globalInterceptors = List.copyOf(globalInterceptors);
        }
        this.store = store != null ? store : new InMemoryStateStore();
        this.locale = locale != null ? locale : Locale.getDefault();
        this.botPattern = botPattern != null ? botPattern : "";
        this.requestsPerSecond = requestsPerSecond != null ? requestsPerSecond : 30;

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
}
