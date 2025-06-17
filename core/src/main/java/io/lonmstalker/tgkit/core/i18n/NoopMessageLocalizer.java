package io.lonmstalker.tgkit.core.i18n;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

public class NoopMessageLocalizer implements MessageLocalizer {

    @Override
    public void setLocale(@NonNull Locale locale) {
        // no-op
    }

    @Override
    public void resetLocale() {
        // no-op
    }

    @Override
    public @NonNull String get(@NonNull String key) {
        return key;
    }

    @Override
    public @NonNull String get(@NonNull String key, @NonNull String defaultValue) {
        return defaultValue;
    }

    @Override
    public @NonNull String get(@NonNull String key, @NonNull Object... args) {
        return key;
    }

    @Override
    public @NonNull String get(@NonNull String key, @NonNull String defaultValue, Object... args) {
        return defaultValue;
    }
}
