package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

/** Фабрика кнопок. */
public interface Button {

  /** Создаёт экземпляр кнопки. */
  @NonNull InlineKeyboardButton build(MessageLocalizer loc);

  /** Кнопка с callback data. */
  static @NonNull Button cb(@NonNull String label, @NonNull String data) {
    return req -> InlineKeyboardButton.builder().text(label).callbackData(data).build();
  }

  /** Обычная кнопка. */
  static @NonNull Button btn(@NonNull String label) {
    return cb(label, label);
  }

  /** Обычная кнопка из i18n. */
  static @NonNull Button btnKey(@NonNull String key, @NonNull Object... args) {
    return loc -> {
      String text = loc != null ? loc.get(key, args) : key;
      return InlineKeyboardButton.builder().text(text).callbackData(text).build();
    };
  }

  /** Ссылка. */
  static @NonNull Button url(@NonNull String label, @NonNull String url) {
    return loc -> InlineKeyboardButton.builder().text(label).url(url).build();
  }

  /** Ссылка с текстом из i18n. */
  static @NonNull Button urlKey(@NonNull String key, @NonNull String url, @NonNull Object... args) {
    return loc ->
        InlineKeyboardButton.builder()
            .text(loc != null ? loc.get(key, args) : key)
            .url(url)
            .build();
  }

  /** Веб‑приложение. */
  static @NonNull Button webApp(@NonNull String label, @NonNull String url) {
    return loc -> InlineKeyboardButton.builder().text(label).webApp(new WebAppInfo(url)).build();
  }

  /** Веб‑приложение с текстом из i18n. */
  static @NonNull Button webAppKey(
      @NonNull String key, @NonNull String url, @NonNull Object... args) {
    return loc ->
        InlineKeyboardButton.builder()
            .text(loc != null ? loc.get(key, args) : key)
            .webApp(new WebAppInfo(url))
            .build();
  }

  /** Кнопка с callback и текстом из i18n. */
  static @NonNull Button cbKey(@NonNull String key, @NonNull String data, @NonNull Object... args) {
    return loc ->
        InlineKeyboardButton.builder()
            .text(loc != null ? loc.get(key, args) : key)
            .callbackData(data)
            .build();
  }
}
