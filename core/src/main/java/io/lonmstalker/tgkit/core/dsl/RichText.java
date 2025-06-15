package io.lonmstalker.tgkit.core.dsl;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Построитель форматированного текста Markdown.
 */
public final class RichText {
    private final StringBuilder sb = new StringBuilder();

    public static @NonNull RichText text() {
        return new RichText();
    }

    /**
     * Полужирный текст.
     */
    public @NonNull RichText bold(@NonNull String text) {
        sb.append("**").append(text).append("**");
        return this;
    }

    /**
     * Ссылка.
     */
    public @NonNull RichText url(@NonNull String label,
                                 @NonNull String url) {
        sb.append("[").append(label).append("](").append(url).append(")");
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
