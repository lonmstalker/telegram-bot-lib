package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
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

public class TelegramSender extends DefaultAbsSender {

    protected TelegramSender(@NonNull DefaultBotOptions options,
                             @NonNull String botToken) {
        super(options, botToken);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws BotApiException {
        return withConvertException(() -> super.execute(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> executeAsync(
            Method method) throws BotApiException {
        return withConvertException(() -> super.executeAsync(method));
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>> void executeAsync(
            Method method, Callback callback) throws BotApiException {
        withConvertException(() -> {
            super.executeAsync(method, callback);
            return null;
        });
    }

    @Override
    protected <T extends Serializable, Method extends BotApiMethod<T>> CompletableFuture<T> sendApiMethodAsync(
            Method method) throws BotApiException {
        return withConvertException(() -> super.sendApiMethodAsync(method));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAnimation sendAnimation) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendAnimation));
    }

    @Override
    public CompletableFuture<Serializable> executeAsync(EditMessageMedia editMessageMedia) throws BotApiException {
        return withConvertException(() -> super.executeAsync(editMessageMedia));
    }

    @Override
    public CompletableFuture<File> executeAsync(UploadStickerFile uploadStickerFile) throws BotApiException {
        return withConvertException(() -> super.executeAsync(uploadStickerFile));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return withConvertException(() -> super.executeAsync(createNewStickerSet));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return withConvertException(() -> super.executeAsync(setStickerSetThumbnail));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(AddStickerToSet addStickerToSet) throws BotApiException {
        return withConvertException(() -> super.executeAsync(addStickerToSet));
    }

    @Override
    public CompletableFuture<Boolean> executeAsync(SetChatPhoto setChatPhoto) throws BotApiException {
        return withConvertException(() -> super.executeAsync(setChatPhoto));
    }

    @Override
    public CompletableFuture<List<Message>> executeAsync(SendMediaGroup sendMediaGroup) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendMediaGroup));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVoice sendVoice) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendVoice));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendAudio sendAudio) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendAudio));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendSticker sendSticker) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendSticker));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideoNote sendVideoNote) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendVideoNote));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendVideo sendVideo) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendVideo));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendPhoto sendPhoto) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendPhoto));
    }

    @Override
    public CompletableFuture<Message> executeAsync(SendDocument sendDocument) throws BotApiException {
        return withConvertException(() -> super.executeAsync(sendDocument));
    }

    @Override
    public Message execute(SendAnimation sendAnimation) throws BotApiException {
        return withConvertException(() -> super.execute(sendAnimation));
    }

    @Override
    public Serializable execute(EditMessageMedia editMessageMedia) throws BotApiException {
        return withConvertException(() -> super.execute(editMessageMedia));
    }

    @Override
    public File execute(UploadStickerFile uploadStickerFile) throws BotApiException {
        return withConvertException(() -> super.execute(uploadStickerFile));
    }

    @Override
    public Boolean execute(CreateNewStickerSet createNewStickerSet) throws BotApiException {
        return withConvertException(() -> super.execute(createNewStickerSet));
    }

    @Override
    public Boolean execute(SetStickerSetThumbnail setStickerSetThumbnail) throws BotApiException {
        return withConvertException(() -> super.execute(setStickerSetThumbnail));
    }

    @Override
    public Boolean execute(AddStickerToSet addStickerToSet) throws BotApiException {
        return withConvertException(() -> super.execute(addStickerToSet));
    }

    @Override
    public List<Message> execute(SendMediaGroup sendMediaGroup) throws BotApiException {
        return withConvertException(() -> super.execute(sendMediaGroup));
    }

    @Override
    public Boolean execute(SetChatPhoto setChatPhoto) throws BotApiException {
        return withConvertException(() -> super.execute(setChatPhoto));
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
}
