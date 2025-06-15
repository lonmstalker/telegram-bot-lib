package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель опроса. */
public class PollBuilder extends BotResponse.CommonBuilder<PollBuilder> {
    private final String question;
    private final List<String> options = new ArrayList<>();
    private boolean anonymous = true;

    PollBuilder(@NonNull BotRequest<?> req, @NonNull String question) {
        super(req);
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
    public @NonNull PartialBotApiMethod<?> build() {
        SendPoll poll = new SendPoll();
        poll.setChatId(String.valueOf(chatId));
        poll.setQuestion(question);
        poll.setOptions(options);
        poll.setIsAnonymous(anonymous);
        return poll;
    }
}
