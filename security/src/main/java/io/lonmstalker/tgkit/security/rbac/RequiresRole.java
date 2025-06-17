package io.lonmstalker.tgkit.security.rbac;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(RequiresRoles.class)
public @interface RequiresRole {
    /** Одна или несколько ролей; достаточно совпадения любой. */
    String[] value();
}
