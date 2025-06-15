package io.lonmstalker.observability;

import io.micrometer.core.instrument.Tag;

/**
 * Набор тегов метрик.
 *
 * @param items массив тегов Micrometer
 */
public record Tags(Tag... items) {

    /**
     * Создаёт объект {@link Tags} из набора тегов.
     *
     * @param items массив тегов
     * @return новый объект
     */
    public static Tags of(Tag... items) {
        return new Tags(items);
    }
}
