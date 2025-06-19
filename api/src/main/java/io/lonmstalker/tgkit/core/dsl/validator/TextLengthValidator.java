package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.Nullable;

/* Text <= 4096 */
public final class TextLengthValidator implements Validator<String> {

    @Override
    public void validate(@Nullable String t) {
        if (t != null && t.codePointCount(0, t.length()) > 4096)
            throw new BotApiException("Message text exceeds 4096 chars");
    }
}