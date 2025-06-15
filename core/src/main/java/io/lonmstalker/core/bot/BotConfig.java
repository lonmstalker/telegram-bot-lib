package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotExceptionHandler;
import io.lonmstalker.core.interceptor.BotInterceptor;
import io.lonmstalker.core.state.InMemoryStateStore;
import io.lonmstalker.core.state.StateStore;
import lombok.Getter;
import lombok.Setter;
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
    private @NonNull List<BotInterceptor> globalInterceptors = List.of();
    private @NonNull StateStore store = new InMemoryStateStore();
    private @NonNull String botPattern = "";
    private int requestsPerSecond = 30;
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
}
