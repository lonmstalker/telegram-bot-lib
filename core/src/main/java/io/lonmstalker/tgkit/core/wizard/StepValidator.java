package io.lonmstalker.tgkit.core.wizard;

@FunctionalInterface
public interface StepValidator {
    boolean validate(String value);

    /** Валидатор по умолчанию, всегда возвращает true. */
    class Identity implements StepValidator {
        @Override public boolean validate(String value) { return true; }
    }
}
