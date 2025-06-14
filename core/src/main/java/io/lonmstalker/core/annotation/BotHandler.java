package io.lonmstalker.core.annotation;

import io.lonmstalker.core.BotCommandOrder;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotHandlerConverter;
import io.lonmstalker.core.matching.AlwaysMatch;
import io.lonmstalker.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotHandler {
    @NonNull BotRequestType type();

    Class<? extends CommandMatch<?>> matcher() default AlwaysMatch.class;

    Class<? extends BotHandlerConverter<?>> converter() default BotHandlerConverter.Identity.class;

    int order() default BotCommandOrder.LAST;
}
