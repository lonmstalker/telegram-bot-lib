package io.lonmstalker.core.interceptor;

import io.lonmstalker.core.BotResponse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotInterceptor {

    void preHandle(@NonNull Update update);

    void postHandle(@NonNull Update update);

    void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) throws Exception;
}
