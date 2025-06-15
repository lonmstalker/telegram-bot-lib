package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/** Построитель отправки фото. */
public final class PhotoBuilder extends BotResponse.CommonBuilder<PhotoBuilder> {
    private final InputFile file;
    private String caption;

    PhotoBuilder(InputFile file) {
        this.file = file;
    }

    /** Подпись к фото. */
    public PhotoBuilder caption(String text) {
        this.caption = text;
        return this;
    }

    @Override
    protected BotApiMethod<?> build() {
        SendPhoto photo = new SendPhoto(String.valueOf(chatId), file);
        photo.setCaption(caption);
        if (keyboard != null) photo.setReplyMarkup(keyboard.build());
        photo.setDisableNotification(disableNotif);
        return photo;
    }
}
