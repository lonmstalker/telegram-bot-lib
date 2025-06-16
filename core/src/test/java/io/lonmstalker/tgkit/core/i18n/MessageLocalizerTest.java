package io.lonmstalker.tgkit.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Locale;

public class MessageLocalizerTest {

    @Test
    void russianLocale() {
        MessageLocalizer localizer = new MessageLocalizerImpl("i18n/messages", Locale.forLanguageTag("ru"));
        assertEquals("Понг", localizer.get("command.ping.response"));
    }

    @Test
    void formatArgs() {
        MessageLocalizer loc = new MessageLocalizerImpl("i18n/messages", Locale.forLanguageTag("ru"));
        assertEquals("Сколько будет 1 + 2?", loc.get("captcha.math.question", 1, 2));
    }
}
