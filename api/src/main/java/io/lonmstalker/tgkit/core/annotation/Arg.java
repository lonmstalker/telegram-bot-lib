package io.lonmstalker.tgkit.core.annotation;

import io.lonmstalker.tgkit.core.args.BotArgumentConverter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {

    /** Имя именованной regex-группы из ближайшего матчера. */
    @NonNull String value() default "";

    /** Обязательность; для примитивов по умолчанию false запрещён. */
    boolean required() default true;

    /** Строковый дефолт; применяется до конвертации. */
    String defaultValue() default "";

    /** Пользовательский конвертер; Identity = авто-выбор по типу. */
    Class<? extends BotArgumentConverter<?, ?>> converter() default BotArgumentConverter.UpdateConverter.class;
}
