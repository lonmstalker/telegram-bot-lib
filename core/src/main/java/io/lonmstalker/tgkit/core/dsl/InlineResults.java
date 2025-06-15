package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.inputmessagecontent.InputTextMessageContent;

/** Построитель результатов инлайн‑запроса. */
public final class InlineResults {
    private final List<InlineQueryResult> list = new ArrayList<>();

    public static InlineResults build() {
        return new InlineResults();
    }

    /** Статья. */
    public InlineResults article(String id, String title, String text) {
        InlineQueryResultArticle a = new InlineQueryResultArticle();
        a.setId(id);
        a.setTitle(title);
        a.setInputMessageContent(new InputTextMessageContent(text));
        list.add(a);
        return this;
    }

    /** Фото. */
    public InlineResults photo(String id, String url, String thumb) {
        InlineQueryResultPhoto p = new InlineQueryResultPhoto();
        p.setId(id);
        p.setPhotoUrl(url);
        p.setThumbUrl(thumb);
        list.add(p);
        return this;
    }

    public List<InlineQueryResult> results() {
        return list;
    }
}
