package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель медиа-группы. */
public final class MediaGroupBuilder extends BotResponse.CommonBuilder<MediaGroupBuilder> {
    private final List<InputMedia> items = new ArrayList<>();

    MediaGroupBuilder(@NonNull BotRequest<?> req) {
        super(req);
    }

    /** Фото с подписью. */
    public @NonNull MediaGroupBuilder photo(@NonNull InputFile file, @NonNull String cap) {
        InputMediaPhoto ph = new InputMediaPhoto();
        ph.setMedia(file.getNewMediaFile(), file.getMediaName());
        ph.setCaption(cap);
        items.add(ph);
        return this;
    }

    /** Видео. */
    public @NonNull MediaGroupBuilder video(@NonNull InputFile file) {
        InputMediaVideo v = new InputMediaVideo();
        v.setMedia(file.getNewMediaFile(), file.getMediaName());
        items.add(v);
        return this;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        SendMediaGroup group = new SendMediaGroup();
        group.setChatId(String.valueOf(chatId));
        group.setMedias(items);
        return group;
    }

    @Override
    public @NonNull BotResponse send(@NonNull TelegramTransport tg) {
        List<List<InputMedia>> chunks = new ArrayList<>();
        List<InputMedia> cur = new ArrayList<>();
        for (InputMedia m : items) {
            cur.add(m);
            if (cur.size() == 10) {
                chunks.add(cur);
                cur = new ArrayList<>();
            }
        }
        if (!cur.isEmpty()) {
            chunks.add(cur);
        }
        for (List<InputMedia> c : chunks) {
            SendMediaGroup g = new SendMediaGroup();
            g.setChatId(String.valueOf(chatId));
            g.setMedias(c);
            tg.execute(g);
        }
        return BotResponse.EMPTY;
    }
}
