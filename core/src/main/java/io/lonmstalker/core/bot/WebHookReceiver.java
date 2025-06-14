package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.exception.BotExceptionHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
class WebHookReceiver extends TelegramWebhookBot {
    private final @NonNull String token;
    private final @NonNull BotAdapter adapter;
    private final @NonNull BotExceptionHandler globalExceptionHandler;
    
    @Setter
    private @NonNull String username;

    public WebHookReceiver(@NonNull DefaultBotOptions options,
                           @NonNull BotAdapter adapter,
                           @NonNull String token,
                           @Nullable BotExceptionHandler globalExceptionHandler) {
        super(options, token);
        this.token = token;
        this.adapter = adapter;
        this.globalExceptionHandler = globalExceptionHandler != null
                ? globalExceptionHandler
                : (update, ex) -> log.error("onUpdate with error: ", ex);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
       try {
           return adapter.handle(update);
       } catch (Exception e) {
           globalExceptionHandler.handle(update, e);
           return null;
       }
    }

    @Override
    public String getBotPath() {
        return token;
    }
}
