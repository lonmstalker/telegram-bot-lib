package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import io.lonmstalker.tgkit.core.BotRequest;

/** Построитель викторины. */
public final class QuizBuilder extends PollBuilder {
    private final int correct;

    QuizBuilder(@NonNull BotRequest<?> req,
                @NonNull String q,
                int correct) {
        super(req, q);
        this.correct = correct;
    }

    @Override
    public @NonNull PartialBotApiMethod<?> build() {
        SendPoll poll = (SendPoll) super.build();
        poll.setType("quiz");
        poll.setCorrectOptionId(correct);
        return poll;
    }
}
