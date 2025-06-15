package io.lonmstalker.tgkit.core.parse_mode;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Pattern;

/**
 * Утилитный класс для экранирования спецсимволов в разных форматах:
 * HTML, Markdown и MarkdownV2 (для Telegram Bot API).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sanitizer {

    // Используем regex для одновременного экранирования
    private static final Pattern MARKDOWN2_PATTERN =
            Pattern.compile("([_*\\[\\]()~`>#+\\-=|{}.!])");

    /**
     * Основной метод: возвращает «безопасную» строку для заданного режима.
     *
     * @param input исходный текст
     * @param mode  выбранный режим экранирования
     * @return экранированная строка
     */
    public static @NonNull String sanitize(@NonNull String input,
                                           @NonNull ParseMode mode) {
        return switch (mode) {
            case HTML -> sanitizeHtml(input);
            case MARKDOWN -> sanitizeMarkdown(input);
            case MARKDOWN_V2 -> sanitizeMarkdownV2(input);
        };
    }

    // =======================
    // HTML-экранирование
    // =======================
    private static @NonNull String sanitizeHtml(@NonNull String text) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char c : text.toCharArray()) {
            switch (c) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&#x27;");
                    break;
                case '/':
                    sb.append("&#x2F;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    // =======================
    // Markdown (Telegram Bot API v1)
    // Экранируем: '_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!'
    // =======================
    private static @NonNull String sanitizeMarkdown(@NonNull String text) {
        // В в1 спецификации Telegram не требует экранировать все символы,
        // но мы экранируем минимально: '_', '*', '`', '['
        return text
                .replace("_", "\\_")
                .replace("*", "\\*")
                .replace("`", "\\`")
                .replace("[", "\\[")
                .replace("]", "\\]");
    }

    // =======================
    // MarkdownV2 (Telegram Bot API v2)
    // Полный набор спецсимволов:
    // '_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}', '.', '!'
    // =======================
    private static @NonNull String sanitizeMarkdownV2(@NonNull String text) {
        return MARKDOWN2_PATTERN.matcher(text).replaceAll("\\\\$1");
    }
}
