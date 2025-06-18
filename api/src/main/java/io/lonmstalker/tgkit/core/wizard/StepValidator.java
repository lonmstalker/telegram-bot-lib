package io.lonmstalker.tgkit.core.wizard;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface StepValidator {
    boolean validate(@NonNull String value);

    /** Валидатор по умолчанию, всегда возвращает true. */
    class Identity implements StepValidator {
        @Override public boolean validate(@NonNull String value) { return true; }
    }
}
