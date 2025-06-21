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
package io.github.tgkit.internal.dsl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.tgkit.internal.i18n.MessageLocalizer;
import io.github.tgkit.testkit.TestBotBootstrap;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

class KbBuilderTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void gridArrangesButtons() {
    MessageLocalizer loc = mock(MessageLocalizer.class);
    KbBuilder kb = new KbBuilder(loc).grid(2, Button.btn("A"), Button.btn("B"), Button.btn("C"));

    InlineKeyboardMarkup mk = kb.build();

    // ожидание: первая строка 2 кнопки, вторая — 1
    assertThat(mk.getKeyboard()).hasSize(2);
    assertThat(mk.getKeyboard().get(0)).hasSize(2);
    assertThat(mk.getKeyboard().get(1)).hasSize(1);
  }
}
