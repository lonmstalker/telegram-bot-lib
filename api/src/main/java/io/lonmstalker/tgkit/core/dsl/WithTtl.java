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

import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.core.ttl.DeleteTask;
import io.github.tgkit.core.ttl.TtlPolicy;
import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

public final class WithTtl<T extends BotDSL.CommonBuilder<T, D>, D extends PartialBotApiMethod<?>> {
  private final @NonNull Duration d;
  private final @NonNull DSLContext ctx;
  private final BotDSL.@NonNull CommonBuilder<T, D> b;

  private TtlPolicy policy = TtlPolicy.defaults();
  private Consumer<Exception> onError = ex -> {
  };
  private Consumer<Long> onSuccess = id -> {
  };

  WithTtl(@NonNull Duration d, @NonNull DSLContext ctx, BotDSL.@NonNull CommonBuilder<T, D> b) {
    this.b = b;
    this.d = d;
    this.ctx = ctx;
  }

  public @NonNull WithTtl<T, D> onError(@NonNull Consumer<Exception> c) {
    onError = c;
    return this;
  }

  public @NonNull WithTtl<T, D> onSuccess(@NonNull Consumer<Long> c) {
    onSuccess = c;
    return this;
  }

  public @NonNull WithTtl<T, D> policy(@NonNull TtlPolicy p) {
    policy = p;
    return this;
  }

  public Common<T, D> done() {
    /*
     * Подписываемся на success-callback билдерa ОДИН раз.
     * После каждой фактической отправки сообщения планируем DeleteMessage.
     */
    b.hooks(
        msgId -> {
          Long chat = Objects.requireNonNull(ctx.userInfo().chatId());
          Runnable action =
              () ->
                  ctx.service()
                      .sender()
                      .execute(
                          new DeleteMessage(
                              Objects.requireNonNull(chat).toString(), msgId.intValue()));
          BotGlobalConfig.INSTANCE
              .dsl()
              .getTtlScheduler()
              .schedule(new DeleteTask(chat, msgId, action), d, policy);
          onSuccess.accept(msgId);
        },
        ex -> onError.accept((Exception) ex));

    return b;
  }
}
