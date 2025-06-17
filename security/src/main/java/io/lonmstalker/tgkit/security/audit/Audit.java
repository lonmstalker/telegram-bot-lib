package io.lonmstalker.tgkit.security.audit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {

    AuditField[] value() default {};

    Class<? extends AuditConverter> converter() default AuditConverter.class;
}
