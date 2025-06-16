package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BotArgumentConverter<T> {

    @NonNull
    T convert(@NonNull String raw, @NonNull Context ctx) throws BotApiException;

    final class Identity implements BotArgumentConverter<Object> {
        @Override
        public @NonNull Object convert(@NonNull String raw, @NonNull Context ctx) {
            return raw;
        }
    }
}
