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
package io.github.tgkit.validator.impl;

import io.github.tgkit.api.i18n.MessageKey;
import io.github.tgkit.api.validator.Validator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;

/**
 * Валидаторы для опросов (Poll из Telegram API).
 *
 * <p>Проверяют количество вариантов и длину текста каждого варианта.
 */
public final class PollValidators {

  private static final int MIN_OPTS = 2, MAX_OPTS = 10, MAX_TEXT = 100;

  private PollValidators() {}

  /**
   * Проверяет, что количество вариантов находится в диапазоне [2..10].
   *
   * @return Validator<Poll> с ключом "error.poll.count"
   */
  public static Validator<@NonNull Poll> optionsCount() {
    return Validator.of(
        p -> {
          List<?> opts = p.getOptions();
          return opts != null && opts.size() >= MIN_OPTS && opts.size() <= MAX_OPTS;
        },
        MessageKey.of("error.poll.count", MIN_OPTS, MAX_OPTS));
  }

  /**
   * Проверяет, что каждый вариант не длиннее 100 символов.
   *
   * @return Validator<Poll> с ключом "error.poll.optionLength"
   */
  public static Validator<@NonNull Poll> optionTextLength() {
    return Validator.of(
        p -> p.getOptions().stream().allMatch(o -> o.getText().length() <= MAX_TEXT),
        MessageKey.of("error.poll.optionLength", MAX_TEXT));
  }
}
