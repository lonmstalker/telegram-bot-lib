package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;

/* Caption <= 1024 */
public final class CaptionValidator implements Validator<String> {
    @Override public void validate(String c) {
        if (c != null && c.codePointCount(0, c.length()) > 1024)
            throw new BotApiException("Caption exceeds 1024 chars");
    }
}