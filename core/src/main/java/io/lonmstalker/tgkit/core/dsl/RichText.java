package io.lonmstalker.tgkit.core.dsl;

/**
 * Построитель форматированного текста Markdown.
 */
public final class RichText {
    private final StringBuilder sb = new StringBuilder();

    public static RichText text() {
        return new RichText();
    }

    /** Полужирный текст. */
    public RichText bold(String text) {
        sb.append("**").append(text).append("**");
        return this;
    }

    /** Ссылка. */
    public RichText url(String label, String url) {
        sb.append("[").append(label).append("](").append(url).append(")");
        return this;
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
