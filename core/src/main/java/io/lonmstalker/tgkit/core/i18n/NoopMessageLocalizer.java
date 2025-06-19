package io.lonmstalker.tgkit.core.i18n;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

/**
 * Заглушка {@link MessageLocalizer}, возвращающая ключ без изменений.
 *
 * <p>Полезно, когда поддержка локализации не нужна. Пример использования:
 *
 * <pre>{@code
 * MessageLocalizer localizer = new NoopMessageLocalizer();
 * String text = localizer.get("hello.world"); // "hello.world"
 * }
 * </pre>
 */
public class NoopMessageLocalizer implements MessageLocalizer {

    /** Устанавливает текущую локаль. Значение игнорируется. */
    @Override
    public void setLocale(@NonNull Locale locale) {
        // no-op
    }

    /** Сбрасывает локаль к умолчанию. Ничего не делает. */
    @Override
    public void resetLocale() {
        // no-op
    }

    /**
     * Возвращает переданный ключ без изменений.
     */
    @Override
    public @NonNull String get(@NonNull MessageKey key) {
        return key.key();
    }

    /**
     * Возвращает переданный ключ без изменений.
     */
    @Override
    public @NonNull String get(@NonNull String key) {
        return key;
    }

    /**
     * Возвращает указанное значение по умолчанию.
     */
    @Override
    public @NonNull String get(@NonNull String key, @NonNull String defaultValue) {
        return defaultValue;
    }

    /**
     * Возвращает ключ, игнорируя аргументы форматирования.
     */
    @Override
    public @NonNull String get(@NonNull String key, @NonNull Object... args) {
        return key;
    }

    /**
     * Возвращает значение по умолчанию, не учитывая аргументы.
     */
    @Override
    public @NonNull String get(@NonNull String key, @NonNull String defaultValue, Object... args) {
        return defaultValue;
    }
}
