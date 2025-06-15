package io.craftbot.security;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
@Repeatable(RateLimits.class)
public @interface RateLimit {
    LimiterKey key();
    int permits();
    int seconds();
}

@Retention(RUNTIME)
@Target(METHOD)
@interface RateLimits {
    RateLimit[] value();
}
