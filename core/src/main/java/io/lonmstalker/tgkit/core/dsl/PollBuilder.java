package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

/** Построитель опроса. */
public class PollBuilder extends BotResponse.CommonBuilder<PollBuilder> {
    private final String question;
    private final List<String> options = new ArrayList<>();
    private boolean anonymous = true;

    PollBuilder(String question) {
        this.question = question;
    }

    /** Добавляет вариант ответа. */
    public PollBuilder option(String o) {
        options.add(o);
        return this;
    }

    /** Устанавливает режим анонимности. */
    public PollBuilder anonymous(boolean a) {
        this.anonymous = a;
        return this;
    }

    @Override
    protected BotApiMethod<?> build() {
        SendPoll poll = new SendPoll();
        poll.setChatId(String.valueOf(chatId));
        poll.setQuestion(question);
        poll.setOptions(options);
        poll.setAnonymous(anonymous);
        return poll;
    }
}
