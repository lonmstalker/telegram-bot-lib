/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import java.util.ArrayList;
import java.util.List;
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
