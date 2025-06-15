package io.lonmstalker.core;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Конвертер для преобразования {@link BotRequest} в произвольный тип,
 * используемый обработчиком.
 *
 * @param <T> целевой тип после преобразования
 */
@FunctionalInterface
public interface BotHandlerConverter<T> {

    /**
     * Выполняет преобразование запроса.
     *
     * @param request исходный запрос
     * @return результат преобразования
     */
    @NonNull T convert(@NonNull BotRequest<?> request);

    /**
     * Конвертер по умолчанию, возвращающий исходный запрос без изменений.
     */
    class Identity implements BotHandlerConverter<BotRequest<?>> {
        @Override
        public @NonNull BotRequest<?> convert(@NonNull BotRequest<?> request) {
            return request;
        }
    }
}
