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
package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

class ConditionalBranchesTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void nestedConditionsRunOnce() {
    TelegramSender sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<PartialBotApiMethod<?>>any());

    InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
    ff.enableChat("BETA", 1L);
    BotGlobalConfig.INSTANCE.dsl().featureFlags(ff);

    DSLContext ctx = MockCtx.ctx(1L, 2L, sender);

    AtomicInteger counter = new AtomicInteger();
    Predicate<DSLContext> cond = c -> true;

    new MessageBuilder(ctx, "hi")
        .when(cond, b -> counter.incrementAndGet())
        .onlyAdmin(b -> counter.incrementAndGet())
        .flag("BETA", b -> counter.incrementAndGet())
        .send();

    assertThat(counter).hasValue(3); // каждая ветка ровно раз
  }
}
