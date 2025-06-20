package io.lonmstalker.tgkit.core.parse_mode;

import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Утилитный класс для экранирования спецсимволов в разных форматах: HTML, Markdown и MarkdownV2
 * (для Telegram Bot API).
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sanitizer {

  // Используем regex для одновременного экранирования
  private static final Pattern MARKDOWN2_PATTERN = Pattern.compile("([_*\\[\\]()~`>#+\\-=|{}.!])");

  /**
   * Основной метод: возвращает «безопасную» строку для заданного режима.
   *
   * @param input исходный текст
   * @param mode выбранный режим экранирования
   * @return экранированная строка
   */
  public static @NonNull String sanitize(@NonNull String input, @NonNull ParseMode mode) {
    return switch (mode) {
      case NONE -> input;
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
        case '&' -> sb.append("&amp;");
        case '<' -> sb.append("&lt;");
        case '>' -> sb.append("&gt;");
        case '"' -> sb.append("&quot;");
        case '\'' -> sb.append("&#x27;");
        case '/' -> sb.append("&#x2F;");
        default -> sb.append(c);
      }
    }
    return sb.toString();
  }

  // =======================
  // Markdown (Telegram Bot API v1)
  // Экранируем: '_', '*', '[', ']', '(', ')', '~', '`', '>', '#', '+', '-', '=', '|', '{', '}',
  // '.', '!'
  // =======================
  private static @NonNull String sanitizeMarkdown(@NonNull String text) {
    return text.replace("_", "\\_")
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
