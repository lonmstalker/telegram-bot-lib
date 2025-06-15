package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель викторины. */
public final class QuizBuilder extends PollBuilder {
    private final int correct;

    QuizBuilder(BotRequest<?> req, String q, int correct) {
        super(req, q);
        this.correct = correct;
    }

    @Override
    protected BotApiMethod<?> build() {
        SendPoll poll = (SendPoll) super.build();
        poll.setType("quiz");
        poll.setCorrectOptionId(correct);
        return poll;
    }
}
