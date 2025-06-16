package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;

/**
 * Построитель результатов инлайн‑запроса.
 */
public final class InlineResults {
    private final @NonNull DSLContext ctx;
    private final List<InlineQueryResult> list = new ArrayList<>();

    InlineResults(@NonNull DSLContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Статья.
     */
    public InlineResults article(String id, String title, String text) {
        InlineQueryResultArticle a = new InlineQueryResultArticle();
        a.setId(id);
        a.setTitle(title);
        a.setInputMessageContent(new InputTextMessageContent(text));
        list.add(a);
        return this;
    }

    /**
     * Статья с текстом из i18n.
     */
    public InlineResults articleKey(String id, String titleKey, String textKey, Object... args) {
        return article(
                id,
                ctx.botInfo().localizer().get(titleKey, args),
                ctx.botInfo().localizer().get(textKey, args)
        );
    }

    /**
     * Фото.
     */
    public @NonNull InlineResults photo(@NonNull String id,
                                        @NonNull String url,
                                        @NonNull String thumb) {
        InlineQueryResultPhoto p = new InlineQueryResultPhoto();
        p.setId(id);
        p.setPhotoUrl(url);
        p.setThumbnailUrl(thumb);
        list.add(p);
        return this;
    }

    public @NonNull List<InlineQueryResult> results() {
        return list;
    }
}
