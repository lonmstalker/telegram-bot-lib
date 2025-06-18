package io.lonmstalker.tgkit.observability;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Абстракция над системой трассировки.
 */
public interface Tracer {

    /**
     * Запускает новый span.
     *
     * @param spanName  имя span
     * @param tags теги
     * @return созданный {@link Span}
     */
    Span start(@NonNull String spanName,
               @NonNull Tags tags);
}
