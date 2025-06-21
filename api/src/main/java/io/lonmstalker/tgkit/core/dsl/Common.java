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

import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.dsl.context.DSLContext;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/**
 * Базовый fluent-контракт для всех билдeров DSL.
 *
 * @param <D> тип Telegram-метода, который возвращает {@link #build()}
 * @param <T> конкретный тип билдера (для fluent-цепочек без кастов)
 */
public interface Common<T extends Common<T, D>, D extends PartialBotApiMethod<?>> {

  @NonNull
  T requireChatId();

  @NonNull
  T missingIdStrategy(@NonNull MissingIdStrategy strategy);

  @NonNull
  T replyTo(long msgId);

  @NonNull
  T disableNotif();

  @NonNull
  T keyboard(@NonNull Consumer<KbBuilder> cfg);

  @NonNull
  T when(@NonNull Predicate<DSLContext> cond, @NonNull Consumer<Common<T, D>> branch);

  @NonNull
  T onlyAdmin(@NonNull Consumer<Common<T, D>> branch);

  @NonNull
  T ifFlag(@NonNull String flag, @NonNull Consumer<Common<T, D>> branch);

  @NonNull
  T flag(@NonNull String flag, @NonNull Consumer<Common<T, D>> branch);

  // проверка для userId
  @NonNull
  T flagUser(@NonNull String flag, @NonNull Consumer<Common<T, D>> branch);

  // A/B-сплит: control / variant
  @NonNull
  T abTest(
      @NonNull String key,
      @NonNull Consumer<Common<T, D>> control,
      @NonNull Consumer<Common<T, D>> variant);

  @NonNull
  T hooks(@NonNull Consumer<Long> ok, @NonNull Consumer<Throwable> fail);

  @NonNull
  BotResponse send();

  @NonNull
  CompletableFuture<BotResponse> sendAsync(@NonNull Executor executor);

  @NonNull
  WithTtl ttl(@NonNull Duration d);

  @NonNull
  D build();
}
