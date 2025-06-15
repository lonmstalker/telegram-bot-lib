package io.lonmstalker.tgkit.core.wizard.annotation;

import io.lonmstalker.tgkit.core.wizard.StepValidator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Описывает один шаг Wizard. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Step {
    int order();

    /** Ключ локализации вопроса. */
    @NonNull String askKey();

    /** Значение по умолчанию, если ключ не найден. */
    String defaultAsk() default "";

    /** Ключ для сохранения результата. */
    @NonNull String saveKey();

    /** Валидатор шага. */
    Class<? extends StepValidator> validator() default StepValidator.Identity.class;

    /** Навигационные кнопки. */
    Button[] buttons() default { Button.NEXT, Button.BACK, Button.CANCEL };

    enum Button { NEXT, BACK, CANCEL }
}
