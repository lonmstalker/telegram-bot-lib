package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import org.telegram.telegrambots.meta.api.objects.WebAppInfo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Фабрика кнопок.
 */
public interface Button {

    /** Создаёт экземпляр кнопки. */
    InlineKeyboardButton build(MessageLocalizer loc);

    /** Без локализации. */
    default InlineKeyboardButton build() {
        return build(null);
    }

    /** Кнопка с callback data. */
    static Button cb(String label, String data) {
        return req -> InlineKeyboardButton.builder().text(label).callbackData(data).build();
    }

    /** Обычная кнопка. */
    static Button btn(String label) {
        return cb(label, label);
    }

    /** Обычная кнопка из i18n. */
    static Button btnKey(String key, Object... args) {
        return loc -> {
            String text = loc != null ? loc.get(key, args) : key;
            return InlineKeyboardButton.builder().text(text).callbackData(text).build();
        };
    }

    /** Ссылка. */
    static Button url(String label, String url) {
        return loc -> InlineKeyboardButton.builder().text(label).url(url).build();
    }

    /** Ссылка с текстом из i18n. */
    static Button urlKey(String key, String url, Object... args) {
        return loc -> InlineKeyboardButton.builder()
                .text(loc != null ? loc.get(key, args) : key)
                .url(url)
                .build();
    }

    /** Веб‑приложение. */
    static Button webApp(String label, String url) {
        return loc -> InlineKeyboardButton.builder().text(label).webApp(new WebAppInfo(url)).build();
    }

    /** Веб‑приложение с текстом из i18n. */
    static Button webAppKey(String key, String url, Object... args) {
        return loc -> InlineKeyboardButton.builder()
                .text(loc != null ? loc.get(key, args) : key)
                .webApp(new WebAppInfo(url))
                .build();
    }

    /** Кнопка с callback и текстом из i18n. */
    static Button cbKey(String key, String data, Object... args) {
        return loc -> InlineKeyboardButton.builder()
                .text(loc != null ? loc.get(key, args) : key)
                .callbackData(data)
                .build();
    }
}
