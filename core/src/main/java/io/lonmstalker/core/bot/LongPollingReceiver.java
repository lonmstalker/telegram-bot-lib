package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.exception.BotExceptionHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
class LongPollingReceiver extends TelegramLongPollingBot {
    private final @NonNull BotAdapter adapter;
    private final @NonNull BotExceptionHandler globalExceptionHandler;

    @Setter
    private @NonNull String username;

    public LongPollingReceiver(@NonNull DefaultBotOptions options,
                               @NonNull BotAdapter adapter,
                               @NonNull String token,
                               @Nullable BotExceptionHandler globalExceptionHandler) {
        super(options, token);
        this.adapter = adapter;
        this.globalExceptionHandler = globalExceptionHandler != null
                ? globalExceptionHandler
                : (update, ex) -> log.error("onUpdate with error: ", ex);
    }

    @Override
    public void onUpdateReceived(@NonNull Update update) {
        try {
            execute(adapter.handle(update));
        } catch (Exception e) {
            globalExceptionHandler.handle(update, e);
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
