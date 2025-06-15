package io.craftbot.security;

import io.craftbot.security.spi.CaptchaProvider;
import io.craftbot.security.spi.RateLimitBackend;
import io.craftbot.security.spi.RoleResolver;
import io.craftbot.security.spi.StateStore;

public record SecurityBundle(SecurityInterceptor interceptor) {
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private RoleResolver roles;
        private RateLimitBackend backend;
        private CaptchaProvider captcha;
        private StateStore store;

        public Builder roles(RoleResolver r) { this.roles = r; return this; }
        public Builder backend(RateLimitBackend b) { this.backend = b; return this; }
        public Builder captcha(CaptchaProvider c) { this.captcha = c; return this; }
        public Builder store(StateStore s) { this.store = s; return this; }

        public SecurityBundle build() {
            SecurityInterceptor i = new SecurityInterceptor(roles, backend, captcha, store);
            SecurityBundleHolder.set(i);
            return new SecurityBundle(i);
        }
    }
}
