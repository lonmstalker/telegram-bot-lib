package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

/* MediaGroup max 10 */
public final class MediaGroupSizeValidator implements Validator<List<?>> {

    @Override public void validate(@Nullable List<?> items) {
        if (items == null) {
            throw new BotApiException("Media group size cannot be null");
        }
        if (items.size() > 10)
            throw new BotApiException("Telegram allows max 10 items per media group");
    }
}