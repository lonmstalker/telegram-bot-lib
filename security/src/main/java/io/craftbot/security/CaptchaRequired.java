package io.craftbot.security;

import io.craftbot.security.spi.CaptchaChallenge;

public class CaptchaRequired extends RuntimeException {
    private final CaptchaChallenge challenge;

    public CaptchaRequired(CaptchaChallenge challenge) {
        this.challenge = challenge;
    }

    public CaptchaChallenge challenge() {
        return challenge;
    }
}
