/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.internal.parse_mode;

import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Утилитный класс для экранирования спецсимволов в разных форматах: HTML, Markdown и MarkdownV2
 * (для Telegram Bot API).
 */
public final class Sanitizer {
  // Используем regex для одновременного экранирования
  private static final Pattern MARKDOWN2_PATTERN = Pattern.compile("([_*\\[\\]()~`>#+\\-=|{}.!])");

  private Sanitizer() {}

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
