package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.validator.MediaGroupSizeValidator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

import java.util.ArrayList;
import java.util.List;

/** Построитель медиа-группы. */
public final class MediaGroupBuilder extends BotDSL.CommonBuilder<MediaGroupBuilder, SendMediaGroup> {
    private static final MediaGroupSizeValidator VALIDATOR = new MediaGroupSizeValidator();
    private final List<InputMedia> items = new ArrayList<>();

    MediaGroupBuilder(@NonNull DSLContext ctx) {
        super(ctx);
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
    public @NonNull SendMediaGroup build() {
        SendMediaGroup group = new SendMediaGroup();
        group.setChatId(String.valueOf(chatId));
        group.setMedias(items);
        return group;
    }

    @Override
    public @NonNull BotResponse send() {
        requireChatId();

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
            VALIDATOR.validate(c);
            SendMediaGroup g = new SendMediaGroup();

            g.setChatId(String.valueOf(chatId));
            g.setMedias(c);

            super.ctx.service().sender().execute(g);
        }

        return new BotResponse();
    }
}
