package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;

/* Text <= 4096 */
public final class TextLengthValidator implements Validator<String> {
    @Override public void validate(String t) {
        if (t != null && t.codePointCount(0, t.length()) > 4096)
            throw new BotApiException("Message text exceeds 4096 chars");
    }
}