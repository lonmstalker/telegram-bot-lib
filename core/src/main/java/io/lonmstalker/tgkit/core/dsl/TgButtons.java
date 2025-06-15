package io.lonmstalker.tgkit.core.dsl;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

/**
 * Готовые кнопки.
 */
public final class TgButtons {
    private TgButtons() {}

    /** Кнопка запроса номера телефона. */
    public static KeyboardButton sharePhone() {
        KeyboardButton b = new KeyboardButton("Share phone");
        b.setRequestContact(true);
        return b;
    }
}
