package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.inputmessagecontent.InputTextMessageContent;

/** Построитель результатов инлайн‑запроса. */
public final class InlineResults {
    private final BotRequest<?> req;
    private final MessageLocalizer loc;
    private final List<InlineQueryResult> list = new ArrayList<>();

    InlineResults(BotRequest<?> req) {
        this.req = req;
        this.loc = req != null ? req.botInfo().localizer() : null;
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

    /** Статья с текстом из i18n. */
    public InlineResults articleKey(String id, String titleKey, String textKey, Object... args) {
        return article(id, loc != null ? loc.get(titleKey, args) : titleKey,
                loc != null ? loc.get(textKey, args) : textKey);
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
