package io.lonmstalker.tgkit.core.annotation;

import io.lonmstalker.tgkit.core.BotCommandOrder;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotHandlerConverter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotHandler {

    @NonNull BotRequestType type() default BotRequestType.MESSAGE;

    String botGroup() default "";

    Class<? extends BotHandlerConverter<?>> converter() default BotHandlerConverter.Identity.class;

    int order() default BotCommandOrder.LAST;
}
