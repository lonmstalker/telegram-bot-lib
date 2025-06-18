package io.lonmstalker.tgkit.core.annotation;

import io.lonmstalker.tgkit.core.user.BotUserProvider;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserRoleMatch {
    Class<? extends BotUserProvider> provider();
    String[] roles();
}
