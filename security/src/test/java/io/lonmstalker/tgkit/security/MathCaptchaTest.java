package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.security.impl.MathCaptcha;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Locale;

public class MathCaptchaTest {
    @Test
    void verifyAnswer() {
        MathCaptcha c = MathCaptcha.easy();
        var msg = c.question(1L, new MessageLocalizer(Locale.US));
        String q = msg.getText();
        String[] parts = q.replace("Solve ", "").replace(" = ?", "").split(" \\+");
        int sum = Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
        assertTrue(c.verify(1L, Integer.toString(sum)));
    }
}
