package io.lonmstalker.tgkit.core.validator;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Универсальный валидатор для любых типов {@code T}.
 *
 * @param <T> тип проверяемого значения
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Проверяет значение на корректность.
     *
     * @param value входное значение
     * @throws ValidationException если проверка не прошла
     */
    void validate(@Nullable T value) throws ValidationException;

    /**
     * Создаёт {@code Validator<T>} из произвольного {@link Predicate}
     * и ключа ошибки.
     *
     * @param predicate условие прохождения
     * @param errorKey  ключ для локализованного сообщения об ошибке
     */
    static <T> @NonNull Validator<T> of(@NonNull Predicate<T> predicate,
                                        @NonNull MessageKey errorKey) {
        return v -> {
            if (!predicate.test(v)) {
                throw ValidationException.of(errorKey);
            }
        };
    }

    /**
     * Комбинирует два валидатора: сначала этот, потом другой.
     */
    default @NonNull Validator<T> and(@NonNull Validator<? super T> other) {
        Objects.requireNonNull(other, "other");
        return v -> {
            validate(v);
            other.validate(v);
        };
    }

    /**
     * «Или»: если первый не прошёл, пытаем второй.
     */
    default @NonNull Validator<T> or(@NonNull Validator<? super T> other) {
        Objects.requireNonNull(other, "other");
        return v -> {
            try {
                validate(v);
            } catch (ValidationException ex) {
                other.validate(v);
            }
        };
    }
}