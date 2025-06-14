package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotExceptionHandler;
import io.lonmstalker.core.interceptor.BotInterceptor;
import org.telegram.telegrambots.updatesreceivers.ExponentialBackOff;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;

@Setter
@Getter
public class BotConfig extends DefaultBotOptions {
    private @Nullable BotExceptionHandler globalExceptionHandler;
    private @NonNull List<BotInterceptor> globalInterceptors = List.of();
    private int requestsPerSecond = 30;

    public BotConfig() {
        setBackOff(new ExponentialBackOff());
    }
}
