package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
class WebHookReceiver extends TelegramWebhookBot {
    private final @NonNull String token;
    private final @NonNull BotAdapter adapter;
    private final @NonNull BotExceptionHandler globalExceptionHandler;
    
    @Setter
    private @Nullable String username;

    public WebHookReceiver(@NonNull BotConfig options,
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
    public @NonNull String getBotUsername() {
        return username != null ? username : StringUtils.EMPTY;
    }

    @Override
    @SuppressWarnings("override.return")
    public @Nullable BotApiMethod<?> onWebhookUpdateReceived(Update update) {
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
