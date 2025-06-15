package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
class LongPollingReceiver extends TelegramLongPollingBot {
    private final @NonNull BotAdapter adapter;
    private final @NonNull BotExceptionHandler globalExceptionHandler;

    @Setter
    private @Nullable String username;

    public LongPollingReceiver(@NonNull BotConfig options,
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
            var result = adapter.handle(update);
            if (result != null) {
                execute(result);
            }
        } catch (Exception e) {
            globalExceptionHandler.handle(update, e);
        }
    }

    @Override
    public @NonNull String getBotUsername() {
        return username != null ? username : StringUtils.EMPTY;
    }
}
