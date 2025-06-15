package io.craftbot.security.captcha;

import io.craftbot.security.spi.CaptchaChallenge;
import io.craftbot.security.spi.CaptchaProvider;

public class MathCaptchaProvider implements CaptchaProvider {
    @Override
    public CaptchaChallenge create(long chatId) {
        return new CaptchaChallenge("2+2?", null, null, 60);
    }

    @Override
    public boolean verify(long chatId, String answer) {
        return "4".equals(answer);
    }
}
