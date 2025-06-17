package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

/** Построитель опроса. */
public class PollBuilder extends BotDSL.CommonBuilder<PollBuilder, SendPoll> {
    protected final String question;
    protected final List<String> options = new ArrayList<>();
    protected boolean anonymous = true;

    PollBuilder(@NonNull DSLContext ctx, @NonNull String question) {
        super(ctx);
        this.question = question;
    }

    /** Добавляет вариант ответа. */
    public @NonNull PollBuilder option(@NonNull String o) {
        options.add(o);
        return this;
    }

    /** Устанавливает режим анонимности. */
    public PollBuilder anonymous(boolean a) {
        this.anonymous = a;
        return this;
    }

    @Override
    public @NonNull SendPoll build() {
        requireChatId();

        SendPoll poll = new SendPoll();
        poll.setChatId(String.valueOf(chatId));
        poll.setQuestion(question);
        poll.setOptions(options);
        poll.setIsAnonymous(anonymous);

        return poll;
    }
}
