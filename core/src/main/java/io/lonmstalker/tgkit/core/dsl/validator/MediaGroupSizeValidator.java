package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;

import java.util.List;

/* MediaGroup max 10 */
public final class MediaGroupSizeValidator implements Validator<List<?>> {
    @Override public void validate(List<?> items) {
        if (items.size() > 10)
            throw new BotApiException("Telegram allows max 10 items per media group");
    }
}