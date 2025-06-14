package io.lonmstalker.core.annotation;

import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.exception.BotApiException;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Converts message text to an enum value.
 */
public class EnumValueConverter<E extends Enum<E>> implements BotRequestConverter<E> {
    private final Class<E> enumType;

    public EnumValueConverter(@NonNull Class<E> enumType) {
        this.enumType = enumType;
    }

    @Override
    public @NonNull E convert(@NonNull Update update, @NonNull BotRequestType type) {
        Message msg = update.getMessage();
        if (msg == null || msg.getText() == null) {
            throw new BotApiException("No message text to convert to enum " + enumType.getSimpleName());
        }
        return Enum.valueOf(enumType, msg.getText());
    }
}
