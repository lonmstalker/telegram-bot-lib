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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext.SimpleDSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.junit.jupiter.api.Test;

class DslContextValidationTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void shouldThrowWhenBothIdsNull() {
    BotInfo botInfo = mock(BotInfo.class);
    BotUserInfo user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(null);
    when(user.userId()).thenReturn(null);

    assertThatThrownBy(() -> new SimpleDSLContext(mock(BotService.class), botInfo, user))
        .isInstanceOf(BotApiException.class)
        .hasMessageContaining("Both chatId and userId are null");
  }
}
