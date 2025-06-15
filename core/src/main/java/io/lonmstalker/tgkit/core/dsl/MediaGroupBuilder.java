package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель медиа-группы. */
public final class MediaGroupBuilder extends BotResponse.CommonBuilder<MediaGroupBuilder> {
    private final List<InputMedia> items = new ArrayList<>();

    MediaGroupBuilder(BotRequest<?> req) {
        super(req);
    }

    /** Фото с подписью. */
    public MediaGroupBuilder photo(InputFile file, String cap) {
        InputMediaPhoto ph = new InputMediaPhoto();
        ph.setMedia(file.getNewMediaFile(), file.getMediaName());
        ph.setCaption(cap);
        items.add(ph);
        return this;
    }

    /** Видео. */
    public MediaGroupBuilder video(InputFile file) {
        InputMediaVideo v = new InputMediaVideo();
        v.setMedia(file.getNewMediaFile(), file.getMediaName());
        items.add(v);
        return this;
    }

    @Override
    protected BotApiMethod<?> build() {
        SendMediaGroup group = new SendMediaGroup();
        group.setChatId(String.valueOf(chatId));
        group.setMedias(items);
        return group;
    }

    @Override
    public BotResponse send(TelegramTransport tg) {
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
        return new BotResponse();
    }
}
