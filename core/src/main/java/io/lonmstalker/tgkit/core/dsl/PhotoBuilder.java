package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель отправки фото. */
@SuppressWarnings("initialization.fields.uninitialized")
public final class PhotoBuilder extends BotDSL.CommonBuilder<PhotoBuilder> {
    private final InputFile file;
    private String caption;

    @SuppressWarnings("initialization.fields.uninitialized")
    PhotoBuilder(@NonNull BotRequest<?> req, @NonNull InputFile file) {
        super(req);
        this.file = file;
    }

    /** Подпись к фото. */
    public @NonNull PhotoBuilder caption(@NonNull String text) {
        this.caption = text;
        return this;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        SendPhoto photo = new SendPhoto(String.valueOf(chatId), file);
        photo.setCaption(caption);
        photo.setDisableNotification(disableNotif);
        if (keyboard != null) {
            photo.setReplyMarkup(keyboard.build());
        }
        return photo;
    }
}
