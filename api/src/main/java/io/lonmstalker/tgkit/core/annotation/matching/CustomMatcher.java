package io.lonmstalker.tgkit.core.annotation.matching;

import io.lonmstalker.tgkit.core.matching.CommandMatch;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMatcher {
    Class<? extends CommandMatch<?>> value();
}
