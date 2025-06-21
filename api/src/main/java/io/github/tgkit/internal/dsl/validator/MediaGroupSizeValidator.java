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
package io.github.tgkit.internal.dsl.validator;

import io.github.tgkit.internal.exception.BotApiException;
import io.github.tgkit.internal.validator.Validator;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

/* MediaGroup max 10 */
public final class MediaGroupSizeValidator implements Validator<List<?>> {

  @Override
  public void validate(@Nullable List<?> items) {
    if (items == null) {
      throw new BotApiException("Media group size cannot be null");
    }
    if (items.size() > 10) {
      throw new BotApiException("Telegram allows max 10 items per media group");
    }
  }
}
