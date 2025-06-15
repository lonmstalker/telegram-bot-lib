package io.lonmstalker.tgkit.core.i18n;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.util.Locale;

public class MessageLocalizerTest {

    @Test
    void russianLocale() {
        MessageLocalizer localizer = new MessageLocalizer(Locale.forLanguageTag("ru"));
        assertEquals("Понг", localizer.get("command.ping.response"));
    }
}
