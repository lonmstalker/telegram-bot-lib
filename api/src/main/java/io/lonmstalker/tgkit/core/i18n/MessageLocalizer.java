package io.lonmstalker.tgkit.core.i18n;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;

public interface MessageLocalizer {

    /**
     * Устанавливает локаль для текущего потока.
     *
     * @param locale новая локаль
     */
    void setLocale(@NonNull Locale locale);

    /**
     * Сбрасывает локаль текущего потока на дефолтную.
     */
    void resetLocale();

    /**
     * Получить локализованную строку по ключу.
     * Если ключ не найден, возвращается сам ключ.
     */
    @NonNull String get(@NonNull MessageKey key);

    /**
     * Получить локализованную строку по ключу.
     * Если ключ не найден, возвращается сам ключ.
     */
    @NonNull String get(@NonNull String key);

    /**
     * Получить локализованную строку по ключу.
     * Если ключ не найден, возвращается сам ключ.
     */
    @NonNull String get(@NonNull String key, @NonNull String defaultValue);

    /**
     * Получить локализованную и форматированную строку по ключу с параметрами.
     */
    @NonNull String get(@NonNull String key, @NonNull Object... args);

    /**
     * Получить локализованную и форматированную строку по ключу с параметрами.
     */
    @NonNull String get(@NonNull String key, @NonNull String defaultValue, Object... args);
}
