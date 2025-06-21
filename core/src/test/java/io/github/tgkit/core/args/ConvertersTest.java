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
package io.github.tgkit.core.args;

import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ConvertersTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void unsupported_number_converter() {
    var converter = Converters.getByType(BigDecimal.class);
    assertThrows(BotApiException.class, () -> converter.convert("1", new Context(null, null)));
  }

  @Test
  void primitive_and_boxed_conversion() {
    var intConv = Converters.getByType(int.class);
    assertEquals(5, intConv.convert("5", new Context(null, null)));
    var boolConv = Converters.getByType(Boolean.class);
    assertTrue(boolConv.convert("true", new Context(null, null)));
  }

  @Test
  void enum_conversion() {
    var conv = Converters.getByType(Color.class);
    assertEquals(Color.BLUE, conv.convert("BLUE", new Context(null, null)));
  }

  @Test
  void getByClass_caches_instances() {
    var first = Converters.getByClass(UpperConverter.class);
    var second = Converters.getByClass(UpperConverter.class);
    assertSame(first, second);
    assertEquals(
        "ABC", ((BotArgumentConverter<String>) first).convert("abc", new Context(null, null)));
  }

  enum Color {
    RED,
    BLUE
  }

  static class UpperConverter implements BotArgumentConverter<String> {
    @Override
    public String convert(String raw, Context ctx) {
      return raw.toUpperCase();
    }
  }
}
