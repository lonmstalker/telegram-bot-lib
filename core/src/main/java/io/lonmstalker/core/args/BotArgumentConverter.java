package io.lonmstalker.core.args;

import io.lonmstalker.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BotArgumentConverter<T> {
    T convert(@NonNull String raw, @NonNull Context ctx) throws Exception;

    final class Identity implements BotArgumentConverter<Object> {
        @Override
        public Object convert(String raw, @NonNull Context ctx) {
            return raw;
        }
    }
}
