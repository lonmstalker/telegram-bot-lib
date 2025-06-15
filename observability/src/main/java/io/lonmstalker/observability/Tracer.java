package io.lonmstalker.observability;

import io.opentelemetry.api.common.Attributes;

/**
 * Абстракция над системой трассировки.
 */
public interface Tracer {

    /**
     * Запускает новый span.
     *
     * @param spanName  имя span
     * @param attributes атрибуты
     * @return созданный {@link Span}
     */
    Span start(String spanName, Attributes attributes);
}
