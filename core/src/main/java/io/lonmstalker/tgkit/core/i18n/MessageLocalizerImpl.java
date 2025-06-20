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
