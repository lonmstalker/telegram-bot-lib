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
package io.github.tgkit.internal.args;

import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotArgumentConverter<T, D> {

  @NonNull D convert(@NonNull String raw, @NonNull Context<T> ctx) throws BotApiException;

  default boolean isUpdate() {
    return false;
  }

  default boolean isBotRequest() {
    return false;
  }

  final class UpdateConverter implements BotArgumentConverter<Update, Update> {

    @Override
    public @NonNull Update convert(@NonNull String raw, @NonNull Context<Update> ctx) {
      return ctx.data();
    }

    @Override
    public boolean isUpdate() {
      return true;
    }
  }

  final class RequestConverter
      implements BotArgumentConverter<BotRequest<Object>, BotRequest<Object>> {

    @Override
    public @NonNull BotRequest<Object> convert(
        @NonNull String raw, @NonNull Context<BotRequest<Object>> ctx) throws BotApiException {
      return ctx.data();
    }

    @Override
    public boolean isBotRequest() {
      return BotArgumentConverter.super.isBotRequest();
    }
  }
}
