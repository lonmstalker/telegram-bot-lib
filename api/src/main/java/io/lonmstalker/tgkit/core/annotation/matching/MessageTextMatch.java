package io.lonmstalker.tgkit.core.annotation.matching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageTextMatch {
  String value();

  boolean ignoreCase() default false;
}
