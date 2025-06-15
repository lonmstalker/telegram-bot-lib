package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.state.StateStore;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.client.config.RequestConfig;
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
    private @Nullable BotExceptionHandler globalExceptionHandler;
    @Builder.Default
    private @NonNull List<BotInterceptor> globalInterceptors = List.of();
    @Builder.Default
    private @NonNull StateStore store = new InMemoryStateStore();
    @Builder.Default
    private @NonNull String botPattern = "";
    @Builder.Default
    private int requestsPerSecond = 30;
    @Builder.Default
    private @NonNull Locale locale = Locale.getDefault();

    @SuppressWarnings("method.invocation")
    public BotConfig() {
        setBackOff(new ExponentialBackOff());
    }

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
    @SuppressWarnings("argument")
    public BotConfig(@Nullable BotExceptionHandler globalExceptionHandler,
                     List<BotInterceptor> globalInterceptors,
                     StateStore store,
                     String botPattern,
                     Integer requestsPerSecond,
                     Locale locale,
                     String proxyHost,
                     Integer proxyPort,
                     ProxyType proxyType,
                     String baseUrl,
                     Integer maxThreads,
                     RequestConfig requestConfig,
                     HttpContext httpContext,
                     BackOff backOff,
                     Integer maxWebhookConnections,
                     List<String> allowedUpdates,
                     Integer getUpdatesTimeout,
                     Integer getUpdatesLimit) {
        this();
        this.globalExceptionHandler = globalExceptionHandler;
        if (globalInterceptors != null) {
            this.globalInterceptors = List.copyOf(globalInterceptors);
        }
        if (store != null) {
            this.store = store;
        }
        if (botPattern != null) {
            this.botPattern = botPattern;
        }
        if (requestsPerSecond != null) {
            this.requestsPerSecond = requestsPerSecond;
        }
        if (locale != null) {
            this.locale = locale;
        }
        if (proxyHost != null) {
            setProxyHost(proxyHost);
        }
        if (proxyPort != null) {
            setProxyPort(proxyPort);
        }
        setProxyType(proxyType != null ? proxyType : ProxyType.NO_PROXY);
        if (baseUrl != null) {
            setBaseUrl(baseUrl);
        }
        if (maxThreads != null) {
            setMaxThreads(maxThreads);
        }
        if (requestConfig != null) {
            setRequestConfig(requestConfig);
        }
        if (httpContext != null) {
            setHttpContext(httpContext);
        }
        if (backOff != null) {
            setBackOff(backOff);
        }
        if (maxWebhookConnections != null) {
            setMaxWebhookConnections(maxWebhookConnections);
        }
        if (allowedUpdates != null) {
            setAllowedUpdates(allowedUpdates);
        }
        if (getUpdatesTimeout != null) {
            setGetUpdatesTimeout(getUpdatesTimeout);
        }
        if (getUpdatesLimit != null) {
            setGetUpdatesLimit(getUpdatesLimit);
        }
    }

    public static class BotConfigBuilder {
        public BotConfigBuilder addInterceptor(BotInterceptor interceptor) {
            if (this.globalInterceptors == null) {
                this.globalInterceptors = new java.util.ArrayList<>();
            }
            this.globalInterceptors.add(interceptor);
            return this;
        }
    }
}
