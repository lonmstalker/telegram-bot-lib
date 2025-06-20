package io.lonmstalker.tgkit.core.args;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

public class ConvertersTest {

  static {
    BotCoreInitializer.init();
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
}
