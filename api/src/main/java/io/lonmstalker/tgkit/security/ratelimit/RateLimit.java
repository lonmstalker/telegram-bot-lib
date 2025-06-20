package io.lonmstalker.tgkit.security.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.nullness.qual.NonNull;

@Repeatable(RateLimits.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RateLimit {

  @NonNull LimiterKey key();

  int permits();

  int seconds();
}
