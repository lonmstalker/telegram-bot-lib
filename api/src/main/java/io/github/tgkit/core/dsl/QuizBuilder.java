/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.core.dsl;

import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.core.dsl.validator.PollSpec;
import io.github.tgkit.core.dsl.validator.PollValidator;
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
