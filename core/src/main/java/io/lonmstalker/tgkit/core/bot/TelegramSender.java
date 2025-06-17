package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.stickers.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.generics.BackOff;

public class TelegramSender extends DefaultAbsSender implements AutoCloseable {
    private final BackOff backOff;
    private final RateLimiter rateLimiter;

    public TelegramSender(@NonNull BotConfig options,
                          @NonNull String botToken) {
        this(options, botToken, null);
    }

    public TelegramSender(@NonNull BotConfig options,
                          @NonNull String botToken,
                          @Nullable ScheduledExecutorService scheduler) {
        super(options, botToken);
        this.rateLimiter = new RateLimiter(options.getRequestsPerSecond(), scheduler);
        BackOff tmp = options.getBackOff();
        if (tmp == null) {
            tmp = new ExponentialBackOff();
            options.setBackOff(tmp);
        }
        this.backOff = tmp;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> @NonNull T execute(
            @NonNull Method method) throws BotApiException {
        return executeWithRetry(() -> super.execute(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> @NonNull CompletableFuture<T> executeAsync(
            @NonNull Method method) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void executeAsync(
            @NonNull Method method, @NonNull Callback callback) throws BotApiException {
        executeAsync(method).whenComplete((r, ex) -> {
            if (ex == null) {
                callback.onResult(method, r);
            } else {
                BotApiException botEx = convertToBotApiException(ex);
                callback.onException(method, botEx);
            }
        });
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> @NonNull CompletableFuture<T> sendApiMethodAsync(
            @NonNull Method method) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(method));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(
            @NonNull SendAnimation sendAnimation) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendAnimation));
    }

    @Override
    public @NonNull CompletableFuture<Serializable> executeAsync(
            @NonNull EditMessageMedia editMessageMedia) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(editMessageMedia));
    }

    @Override
    public @NonNull CompletableFuture<File> executeAsync(
            @NonNull UploadStickerFile uploadStickerFile) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(uploadStickerFile));
    }

    @Override
    public @NonNull CompletableFuture<Boolean> executeAsync(
            @NonNull CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(createNewStickerSet));
    }

    @Override
    public @NonNull CompletableFuture<Boolean> executeAsync(
            @NonNull SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(setStickerSetThumbnail));
    }

    @Override
    public @NonNull CompletableFuture<Boolean> executeAsync(
            @NonNull AddStickerToSet addStickerToSet) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(addStickerToSet));
    }

    @Override
    public @NonNull CompletableFuture<Boolean> executeAsync(
            @NonNull SetChatPhoto setChatPhoto) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(setChatPhoto));
    }

    @Override
    public @NonNull CompletableFuture<List<Message>> executeAsync(
            @NonNull SendMediaGroup sendMediaGroup) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendMediaGroup));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendVoice sendVoice) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendVoice)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendAudio sendAudio) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendAudio)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendSticker sendSticker) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendSticker)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendVideoNote sendVideoNote) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendVideoNote)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendVideo sendVideo) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendVideo)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendPhoto sendPhoto) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendPhoto)));
    }

    @Override
    public @NonNull CompletableFuture<Message> executeAsync(@NonNull SendDocument sendDocument) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> executeWithRetry(() -> execute(sendDocument)));
    }

    @Override
    public @NonNull Message execute(@NonNull SendAnimation sendAnimation) throws BotApiException {
        return executeWithRetry(() -> super.execute(sendAnimation));
    }

    @Override
    public @NonNull Serializable execute(@NonNull EditMessageMedia editMessageMedia) throws BotApiException {
        return executeWithRetry(() -> super.execute(editMessageMedia));
    }

    @Override
    public @NonNull File execute(@NonNull UploadStickerFile uploadStickerFile) throws BotApiException {
        return executeWithRetry(() -> super.execute(uploadStickerFile));
    }

    @Override
    public @NonNull Boolean execute(@NonNull CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return executeWithRetry(() -> super.execute(createNewStickerSet));
    }

    @Override
    public @NonNull Boolean execute(@NonNull SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return executeWithRetry(() -> super.execute(setStickerSetThumbnail));
    }

    @Override
    public @NonNull Boolean execute(@NonNull AddStickerToSet addStickerToSet) throws BotApiException {
        return executeWithRetry(() -> super.execute(addStickerToSet));
    }

    @Override
    public @NonNull List<Message> execute(@NonNull SendMediaGroup sendMediaGroup) throws BotApiException {
        return executeWithRetry(() -> super.execute(sendMediaGroup));
    }

    @Override
    public @NonNull Boolean execute(@NonNull SetChatPhoto setChatPhoto) throws BotApiException {
        return executeWithRetry(() -> super.execute(setChatPhoto));
    }

    /**
     * Синхронное выполнение любого PartialBotApiMethod<T>.
     */
    @SuppressWarnings("unchecked")
    public <T extends Serializable> T execute(PartialBotApiMethod<T> method) throws BotApiException {
        return withConvertException(() -> switch (method) {
            case BotApiMethod<?> botApiMethod -> (T) execute(botApiMethod);
            case SendPhoto sendPhoto -> (T) execute(sendPhoto);
            case SendDocument sendDocument -> (T) execute(sendDocument);
            case SendVideo sendVideo -> (T) execute(sendVideo);
            case SendVideoNote sendVideoNote -> (T) execute(sendVideoNote);
            case SendSticker sendSticker -> (T) execute(sendSticker);
            case SendAudio sendAudio -> (T) execute(sendAudio);
            case SendVoice sendVoice -> (T) execute(sendVoice);
            case SendAnimation sendAnimation -> (T) execute(sendAnimation);
            case SendMediaGroup sendMediaGroup -> (T) execute(sendMediaGroup);
            case SetChatPhoto setChatPhoto -> (T) execute(setChatPhoto);
            case CreateNewStickerSet createNewStickerSet -> (T) execute(createNewStickerSet);
            case AddStickerToSet addStickerToSet -> (T) execute(addStickerToSet);
            case UploadStickerFile uploadStickerFile -> (T) execute(uploadStickerFile);
            case EditMessageMedia editMessageMedia -> (T) execute(editMessageMedia);
            default -> throw new BotApiException("Unsupported method: " + method.getMethod());
        });
    }

    private <T> T executeWithRetry(@NonNull RuntimeExceptionExecutor<T> executor) {
        while (true) {
            try {
                rateLimiter.acquire();
                T result = withConvertException(executor);
                backOff.reset();
                return result;
            } catch (BotApiException ex) {
                Throwable cause = ex.getCause();
                if (cause instanceof TelegramApiRequestException req && req.getErrorCode() == 429) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(backOff.nextBackOffMillis());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new BotApiException(ie);
                    }
                } else {
                    throw ex;
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new BotApiException(ie);
            }
        }
    }

    private <T> T withConvertException(@NonNull RuntimeExceptionExecutor<T> executor) {
        try {
            return executor.execute();
        } catch (Exception e) {
            if (e.getCause() == null || e.getMessage() == null) {
                throw new BotApiException(e);
            } else {
                throw new BotApiException(e.getMessage(), e.getCause());
            }
        }
    }

    interface RuntimeExceptionExecutor<T> {
        T execute() throws Exception;
    }

    private BotApiException convertToBotApiException(@NonNull Throwable throwable) {
        if (throwable instanceof java.util.concurrent.CompletionException ce && ce.getCause() != null) {
            throwable = ce.getCause();
        }
        if (throwable instanceof BotApiException be) {
            return be;
        }
        if (throwable.getMessage() == null || throwable.getCause() == null) {
            return new BotApiException(throwable);
        }
        return new BotApiException(throwable.getMessage(), throwable.getCause());
    }

    @Override
    public void close() {
        rateLimiter.close();
    }
}
