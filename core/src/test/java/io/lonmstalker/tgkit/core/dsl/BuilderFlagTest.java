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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.tgkit.core.BotInfo;
import io.github.tgkit.core.BotService;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.github.tgkit.core.user.BotUserInfo;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BuilderFlagTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @BeforeEach
  void setup() {
    InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
    ff.enableChat("EXPERIMENT", 1L);
    ff.enableUser("VIP", 42L);
    ff.rollout("TEXT_EXPERIMENT", 50); // 50 % → chatId < 50 в variant
    BotGlobalConfig.INSTANCE.dsl().featureFlags(ff);
  }

  @Test
  void branchRunsWhenFlagEnabledForChat() {
    AtomicBoolean ran = new AtomicBoolean(false);

    new MessageBuilder(ctx(1L), "hi").flag("EXPERIMENT", b -> ran.set(true)).build();

    assertThat(ran).isTrue();
  }

  @Test
  void branchSkippedIfFlagDisabled() {
    AtomicBoolean ran = new AtomicBoolean(false);
    BotGlobalConfig.INSTANCE.dsl().getFeatureFlags().disableChat("EXPERIMENT", 2);

    new MessageBuilder(ctx(2L), "hi").flag("EXPERIMENT", b -> ran.set(true)).build();

    assertThat(ran).isFalse();
  }

  @Test
  void userFlagTriggersBranch() {
    AtomicBoolean ran = new AtomicBoolean();

    new MessageBuilder(ctx(1L), "vip msg").flagUser("VIP", b -> ran.set(true)).build();

    assertThat(ran).isTrue();
  }

  @Test
  void variantBranchRunsForSelectedChat() {
    AtomicInteger control = new AtomicInteger();
    AtomicInteger variant = new AtomicInteger();

    MessageBuilder mb = new MessageBuilder(ctx(1L), "txt");
    mb.abTest("TEXT_EXPERIMENT", c -> control.incrementAndGet(), v -> variant.incrementAndGet())
        .build();

    assertThat(variant).hasValue(1);
    assertThat(control).hasValue(0);
  }

  @Test
  void controlBranchRunsOtherwise() {
    AtomicInteger control = new AtomicInteger();
    AtomicInteger variant = new AtomicInteger();

    MessageBuilder mb = new MessageBuilder(ctx(1L), "txt");
    mb.abTest("TEXT_EXPERIMENT", c -> control.incrementAndGet(), v -> variant.incrementAndGet())
        .build();

    assertThat(variant.get() + control.get()).isEqualTo(1);
  }

  @Test
  void abTestDoesNothingIfKeyAbsent() {
    AtomicBoolean ran = new AtomicBoolean(false);
    BotGlobalConfig.INSTANCE.dsl().featureFlags(new InMemoryFeatureFlags()); // ничего не задеплоено

    new MessageBuilder(ctx(1L), "text")
        .abTest("UNKNOWN", c -> ran.set(true), v -> ran.set(true))
        .build();

    assertThat(ran).isFalse();
  }

  private DSLContext ctx(Long id) {
    BotInfo botInfo = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(id);
    when(user.userId()).thenReturn(42L);
    return new DSLContext.SimpleDSLContext(mock(BotService.class), botInfo, user);
  }
}
