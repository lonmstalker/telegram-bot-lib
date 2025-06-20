/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Локализатор сообщений, аналогичный Spring MessageSource. */
@SuppressWarnings("type.argument")
public class MessageLocalizerImpl implements MessageLocalizer {

  // Базовое имя ResourceBundle, например "messages"
  private final String baseName;

  // Локаль по умолчанию (если в потоке не установлена своя)
  private final Locale defaultLocale;

  // ThreadLocal для хранения «текущей» локали в потоке
  private final ThreadLocal<@NonNull Locale> threadLocale;

  // Кеш бандлов по локали
  private final Map<Locale, ResourceBundle> bundleCache = new ConcurrentHashMap<>();

  /**
   * Конструктор: берём базовое имя и используем системную локаль по умолчанию.
   *
   * @param baseName имя бандла, например "messages"
   */
  public MessageLocalizerImpl(@NonNull String baseName) {
    this(baseName, Locale.getDefault());
  }

  /**
   * Конструктор с явным указанием «дефолтной» локали.
   *
   * @param baseName имя бандла
   * @param defaultLocale локаль по умолчанию
   */
  @SuppressWarnings("method.invocation")
  public MessageLocalizerImpl(@NonNull String baseName, @NonNull Locale defaultLocale) {
    this.baseName = baseName;
    this.defaultLocale = defaultLocale;
    this.threadLocale = ThreadLocal.withInitial(() -> defaultLocale);
    bundleCache.put(defaultLocale, loadBundle(defaultLocale));
  }

  @Override
  public void setLocale(@NonNull Locale locale) {
    threadLocale.set(locale);
    getBundle(locale);
  }

  public void resetLocale() {
    threadLocale.set(defaultLocale);
  }

  @Override
  public @NonNull String get(@NonNull MessageKey key) {
    return get(key.key(), key.args());
  }

  @Override
  public @NonNull String get(@NonNull String key) {
    Locale locale = threadLocale.get();
    try {
      return getBundle(locale).getString(key);
    } catch (MissingResourceException ex) {
      return key;
    }
  }

  @Override
  public @NonNull String get(@NonNull String key, @NonNull String defaultValue) {
    Locale locale = threadLocale.get();
    try {
      return getBundle(locale).getString(key);
    } catch (MissingResourceException ex) {
      return defaultValue;
    }
  }

  @Override
  public @NonNull String get(@NonNull String key, Object... args) {
    String pattern = get(key);
    // MessageFormat учитывает локаль при форматировании дат/чисел
    return MessageFormat.format(pattern, args);
  }

  @Override
  public @NonNull String get(@NonNull String key, @NonNull String defaultValue, Object... args) {
    String pattern = get(key, "");
    // MessageFormat учитывает локаль при форматировании дат/чисел
    return pattern.isEmpty() ? defaultValue : MessageFormat.format(pattern, args);
  }

  private ResourceBundle loadBundle(Locale locale) {
    return ResourceBundle.getBundle(baseName, locale);
  }

  private ResourceBundle getBundle(Locale locale) {
    return bundleCache.computeIfAbsent(locale, this::loadBundle);
  }
}
