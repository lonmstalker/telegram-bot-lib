package io.lonmstalker.tgkit.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Repeatable(RateLimits.class)
public @interface RateLimit {
    LimiterKey key();
    int permits();
    int seconds();
}
