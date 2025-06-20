package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.validator.PollSpec;
import io.lonmstalker.tgkit.core.dsl.validator.PollValidator;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;

/** Построитель викторины. */
public final class QuizBuilder extends PollBuilder {
  private static final PollValidator VALIDATOR = new PollValidator();
  private final int correct;

  QuizBuilder(@NonNull DSLContext ctx, @NonNull String q, int correct) {
    super(ctx, q);
    this.correct = correct;
  }

  @Override
  @SuppressWarnings("argument")
  public @NonNull SendPoll build() {
    requireChatId();

    PollSpec spec = new PollSpec(question, options, correct);
    VALIDATOR.validate(spec);

    SendPoll poll = super.build();
    poll.setType("quiz");
    poll.setCorrectOptionId(correct);
    poll.setChatId(Objects.requireNonNull(chatId));

    return poll;
  }
}
