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
package io.github.tgkit.core.dsl.validator;

import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.Nullable;

/* Poll / Quiz */
public final class PollValidator implements Validator<PollSpec> {

  @Override
  public void validate(@Nullable PollSpec p) {
    if (p == null) {
      throw new BotApiException("PollSpec is null");
    }
    if (p.question().length() > 300) throw new BotApiException("Poll question > 300 chars");
    if (p.options().isEmpty() || p.options().size() > 10)
      throw new BotApiException("Poll options must be 1..10");
    if (p.correct() != null && p.correct() >= p.options().size())
      throw new BotApiException("correctOptionId out of range");
  }
}
