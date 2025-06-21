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
package io.github.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

public class TelegramSenderTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void convertException() throws Exception {
    TestSender sender = new TestSender();
    Method m =
        TelegramSender.class.getDeclaredMethod(
            "withConvertException", TelegramSender.RuntimeExceptionExecutor.class);
    m.setAccessible(true);
    TelegramSender.RuntimeExceptionExecutor<String> exec =
        () -> {
          throw new Exception("boom");
        };
    var ex = assertThrows(InvocationTargetException.class, () -> m.invoke(sender, exec));
    assertTrue(ex.getCause() instanceof BotApiException);
    assertEquals("boom", ex.getCause().getCause().getMessage());
  }

  static class TestSender extends TelegramSender {
    TestSender() {
      super(BotConfig.builder().build(), "token");
    }
  }
}
