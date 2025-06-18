package io.lonmstalker.tgkit.observability;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Набор тегов метрик.
 *
 * @param items массив тегов Micrometer
 */
public record Tags(@NonNull Tag... items) {

    /**
     * Создаёт объект {@link Tags} из набора тегов.
     *
     * @param items массив тегов
     * @return новый объект
     */
    public static Tags of(@NonNull Tag... items) {
        return new Tags(items);
    }
}
