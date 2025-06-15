package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.objects.WebAppInfo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Фабрика кнопок.
 */
public interface Button {

    /** Создаёт экземпляр кнопки. */
    InlineKeyboardButton build();

    /** Кнопка с callback data. */
    static Button cb(String label, String data) {
        return () -> InlineKeyboardButton.builder().text(label).callbackData(data).build();
    }

    /** Обычная кнопка. */
    static Button btn(String label) {
        return cb(label, label);
    }

    /** Ссылка. */
    static Button url(String label, String url) {
        return () -> InlineKeyboardButton.builder().text(label).url(url).build();
    }

    /** Веб‑приложение. */
    static Button webApp(String label, String url) {
        return () -> InlineKeyboardButton.builder().text(label).webApp(new WebAppInfo(url)).build();
    }
}
