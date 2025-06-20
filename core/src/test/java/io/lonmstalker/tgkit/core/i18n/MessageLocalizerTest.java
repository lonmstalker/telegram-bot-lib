package io.lonmstalker.tgkit.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class MessageLocalizerTest {

  static {
    BotCoreInitializer.init();
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
