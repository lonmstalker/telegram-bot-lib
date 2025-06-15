package io.lonmstalker.tgkit.core.wizard.annotation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Маркирует класс как описание Wizard. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Wizard {
    /** Идентификатор сценария. */
    @NonNull String id();

    /** Время жизни сессии в минутах. */
    int ttlMinutes() default 30;
}
