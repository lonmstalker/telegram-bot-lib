package io.lonmstalker.core;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface BotHandlerConverter<T> {
    @NonNull T convert(@NonNull BotRequest<?> request);

    class Identity implements BotHandlerConverter<BotRequest<?>> {
        @Override
        public @NonNull BotRequest<?> convert(@NonNull BotRequest<?> request) {
            return request;
        }
    }
}
