package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.bots.DefaultAbsSender;
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
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.SetStickerSetThumbnail;
import org.telegram.telegrambots.meta.api.methods.stickers.UploadStickerFile;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;

// rate limit support
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.RateLimiter;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.generics.BackOff;
import io.lonmstalker.core.bot.ExponentialBackOff;

public class TelegramSender extends DefaultAbsSender implements AutoCloseable {

    private final RateLimiter rateLimiter;
    private final BackOff backOff;

    protected TelegramSender(@NonNull BotConfig options,
                             @NonNull String botToken) {
        this(options, botToken, null);
    }

    protected TelegramSender(@NonNull BotConfig options,
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
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws BotApiException {
        return executeWithRetry(() -> super.execute(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(
            Method method) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void executeAsync(
            Method method, Callback callback) throws BotApiException {
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
    protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(
            Method method) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(method));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAnimation sendAnimation) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendAnimation));
    }

    @Override
    public CompletableFuture<Serializable> executeAsync(EditMessageMedia editMessageMedia) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(editMessageMedia));
    }

    @Override
    public CompletableFuture<File> executeAsync(UploadStickerFile uploadStickerFile) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(uploadStickerFile));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(createNewStickerSet));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(setStickerSetThumbnail));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(AddStickerToSet addStickerToSet) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(addStickerToSet));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetChatPhoto setChatPhoto) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(setChatPhoto));
    }

    @Override
    public CompletableFuture<List<Message>> executeAsync(SendMediaGroup sendMediaGroup) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendMediaGroup));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVoice sendVoice) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendVoice));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAudio sendAudio) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendAudio));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendSticker));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideoNote sendVideoNote) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendVideoNote));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideo sendVideo) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendVideo));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendPhoto));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendDocument sendDocument) throws BotApiException {
        return CompletableFuture.supplyAsync(() -> execute(sendDocument));
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws BotApiException {
        return executeWithRetry(() -> super.execute(sendAnimation));
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws BotApiException {
        return executeWithRetry(() -> super.execute(editMessageMedia));
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws BotApiException {
        return executeWithRetry(() -> super.execute(uploadStickerFile));
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return executeWithRetry(() -> super.execute(createNewStickerSet));
    }

    @Override
    public Boolean execute(SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return executeWithRetry(() -> super.execute(setStickerSetThumbnail));
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws BotApiException {
        return executeWithRetry(() -> super.execute(addStickerToSet));
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws BotApiException {
        return executeWithRetry(() -> super.execute(sendMediaGroup));
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws BotApiException {
        return executeWithRetry(() -> super.execute(setChatPhoto));
    }

    private <T> T executeWithRetry(RuntimeExceptionExecutor<T> executor) {
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
                        Thread.sleep(backOff.nextBackOffMillis());
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

    private <T> T withConvertException(RuntimeExceptionExecutor<T> executor) {
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

    private BotApiException convertToBotApiException(Throwable throwable) {
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
