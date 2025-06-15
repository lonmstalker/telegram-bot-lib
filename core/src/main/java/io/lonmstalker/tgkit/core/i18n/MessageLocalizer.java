package io.lonmstalker.tgkit.core.i18n;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class MessageLocalizer {

    private final ResourceBundle bundle;

    public MessageLocalizer() {
        this(Locale.getDefault());
    }

    public MessageLocalizer(@NonNull Locale locale) {
        this.bundle = ResourceBundle.getBundle("i18n.messages", locale);
    }

    public @NonNull String get(@NonNull String key) {
        try {
            return this.bundle.getString(key);
        } catch (MissingResourceException ex) {
            return key;
        }
    }

    public @NonNull String get(@NonNull String key, Object... args) {
        return MessageFormat.format(get(key), args);
    }
}
