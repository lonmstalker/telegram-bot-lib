package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.security.impl.MathCaptcha;

public class SecurityBundle {
    private final RateLimiterBackend backend;
    private final CaptchaProvider captcha;

    private SecurityBundle(RateLimiterBackend backend, CaptchaProvider captcha) {
        this.backend = backend;
        this.captcha = captcha;
    }

    public static Builder builder() {
        return new Builder();
    }

    public SecurityInterceptor interceptor() {
        return new SecurityInterceptor(backend, captcha);
    }

    public static class Builder {
        private RateLimiterBackend backend = InMemoryBackend.create();
        private CaptchaProvider captcha = MathCaptcha.easy();

        public Builder rateLimiter(RateLimiterBackend backend) {
            this.backend = backend;
            return this;
        }

        public Builder captcha(CaptchaProvider captcha) {
            this.captcha = captcha;
            return this;
        }

        public SecurityBundle build() {
            return new SecurityBundle(backend, captcha);
        }
    }
}
