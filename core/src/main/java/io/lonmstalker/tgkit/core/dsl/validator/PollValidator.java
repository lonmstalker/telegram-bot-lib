package io.lonmstalker.tgkit.core.dsl.validator;


import io.lonmstalker.tgkit.core.exception.BotApiException;

/* Poll / Quiz */
public final class PollValidator implements Validator<PollSpec> {
    @Override public void validate(PollSpec p) {
        if (p.question().length() > 300)
            throw new BotApiException("Poll question > 300 chars");
        if (p.options().isEmpty() || p.options().size() > 10)
            throw new BotApiException("Poll options must be 1..10");
        if (p.correct() != null && p.correct() >= p.options().size())
            throw new BotApiException("correctOptionId out of range");
    }
}