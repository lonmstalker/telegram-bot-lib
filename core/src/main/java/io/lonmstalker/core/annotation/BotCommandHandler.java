package io.lonmstalker.core.annotation;

import io.lonmstalker.core.BotCommandOrder;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.bot.BotRequestConverterImpl;
import io.lonmstalker.core.matching.CommandMatch;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface BotCommandHandler {
    BotRequestType type();
    int order() default BotCommandOrder.LAST;
    Class<? extends CommandMatch<?>>[] customMatchers() default {};
    Class<? extends BotRequestConverter<?>> converter() default BotRequestConverterImpl.class;
}
