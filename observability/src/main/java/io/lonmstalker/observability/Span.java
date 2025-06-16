package io.lonmstalker.observability;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Интерфейс абстракции над span системы трассировки.
 */
public interface Span extends AutoCloseable {

    /**
     * Отмечает span как завершившийся с ошибкой.
     *
     * @param t причина ошибки
     */
    void setError(@NonNull Throwable t);

    /**
     * Проставляет тег в span
     * @param tag - ключ
     * @param value - значение
     */
    void setTag(@NonNull String tag, @NonNull String value);

    void close();
}
