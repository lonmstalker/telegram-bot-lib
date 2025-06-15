package io.lonmstalker.observability;

/**
 * Интерфейс абстракции над span системы трассировки.
 */
public interface Span extends AutoCloseable {
    /**
     * Отмечает span как завершившийся с ошибкой.
     *
     * @param t причина ошибки
     */
    void setError(Throwable t);

    @Override
    void close();
}
