package io.lonmstalker.tgkit.core.dsl;

/**
 * Шаблоны клавиатур.
 */
public final class TgKeyboardPresets {
    private TgKeyboardPresets() {}

    /** Клавиатура подтверждения. */
    public static KbBuilder confirmYesNo(Context ctx) {
        return new KbBuilder().row(Button.cb("Yes", "yes"), Button.cb("No", "no"));
    }
}
