package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
class LongPollingReceiver extends TelegramLongPollingBot implements AutoCloseable {
    private final @NonNull BotAdapter adapter;
    private final @NonNull TelegramSender sender;
    private final @NonNull BotExceptionHandler globalExceptionHandler;

    @Setter
    private @Nullable String username;

    @Builder
    public LongPollingReceiver(@NonNull BotConfig options,
                               @NonNull BotAdapter adapter,
                               @NonNull String token,
                               @NonNull TelegramSender sender,
                               @Nullable BotExceptionHandler globalExceptionHandler) {
        super(options, token);
        this.adapter = adapter;
        this.sender = sender;
        this.globalExceptionHandler = globalExceptionHandler != null
                ? globalExceptionHandler
                : (update, ex) -> log.error("onUpdate with error: ", ex);
        if (adapter instanceof BotAdapterImpl b) {
            b.setSender(sender);
        }
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

    @Override
    public void close() throws Exception {
        onClosing();
        if (adapter instanceof AutoCloseable) {
            try {
                ((AutoCloseable) adapter).close();
            } catch (Exception e) {
                // ignored
            }
        }
    }
}
