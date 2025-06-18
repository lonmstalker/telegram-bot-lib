package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;

@FunctionalInterface
public interface Validator<T> {
    void validate(T target) throws BotApiException;

    /** Утилита: прогоняет список валидаторов. */
    static <T> void run(T target, Iterable<Validator<T>> validators) {
        for (Validator<T> v : validators) v.validate(target);
    }
}

