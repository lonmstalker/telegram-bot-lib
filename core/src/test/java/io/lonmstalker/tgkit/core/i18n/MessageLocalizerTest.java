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
package io.lonmstalker.tgkit.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.tgkit.testkit.TestBotBootstrap;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class MessageLocalizerTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void russianLocale() {
    MessageLocalizer localizer =
        new MessageLocalizerImpl("i18n/messages", Locale.forLanguageTag("ru"));
    assertEquals("Понг", localizer.get("command.ping.response"));
  }

  @Test
  void formatArgs() {
    MessageLocalizer loc = new MessageLocalizerImpl("i18n/messages", Locale.forLanguageTag("ru"));
    assertEquals("Сколько будет 1 + 2?", loc.get("captcha.math.question", 1, 2));
  }
}
