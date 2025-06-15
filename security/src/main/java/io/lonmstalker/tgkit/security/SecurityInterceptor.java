package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Простой SecurityInterceptor: проверяет ограничения и роли.
 */
public class SecurityInterceptor implements BotInterceptor {
    private final RateLimiterBackend backend;
    private final CaptchaProvider captcha;

    public SecurityInterceptor(RateLimiterBackend backend, CaptchaProvider captcha) {
        this.backend = backend;
        this.captcha = captcha;
    }

    @Override
    public void preHandle(@NonNull Update update) {
        long chatId = extractChatId(update);
        backend.tryAcquire("GLOBAL", 100, 1); // no-op example
        // здесь могла бы быть более сложная логика
    }

    @Override
    public void postHandle(@NonNull Update update) {}

    @Override
    public void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) {}

    private long extractChatId(Update update) {
        Message msg = update.getMessage();
        if (msg != null) {
            return msg.getChatId();
        }
        if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return 0L;
    }
}
