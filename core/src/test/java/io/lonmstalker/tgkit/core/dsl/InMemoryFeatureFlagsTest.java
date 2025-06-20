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

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.assertj.core.api.Assertions.*;

import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import org.junit.jupiter.api.Test;

class InMemoryFeatureFlagsTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void enableChatAndUser() {
    InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
    ff.enableChat("NEW_MENU", 10L);
    ff.enableUser("VIP", 99L);

    assertThat(ff.isEnabled("NEW_MENU", 10L)).isTrue();
    assertThat(ff.isEnabledForUser("VIP", 99L)).isTrue();
  }

  @Test
  void percentRollout() {
    InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
    ff.rollout("BETA", 30); // 30 %

    // deterministic: chatId % 100
    assertThat(ff.isEnabled("BETA", 5)).isTrue(); // 5 < 30
    assertThat(ff.isEnabled("BETA", 35)).isFalse(); // 35 ≥ 30
  }
}
