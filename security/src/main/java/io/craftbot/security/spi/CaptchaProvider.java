package io.craftbot.security.spi;

public interface CaptchaProvider {
    CaptchaChallenge create(long chatId);
    boolean verify(long chatId, String answer);
}
