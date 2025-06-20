package io.lonmstalker.tgkit.core.i18n;

import java.util.Locale;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Обёртка над ключом для локализованных сообщений и аргументами форматирования. */
public record MessageKey(String key, Object... args) {

  /**
   * @param key уникальный ключ в ресурсах (например, "wizard.reg.name.ask")
   * @param args параметры для {@link String#format(Locale, String, Object...)}
   */
  public MessageKey(@NonNull String key, @NonNull Object... args) {
    this.key = key;
    this.args = args != null ? args.clone() : new Object[0];
  }

  /**
   * @return аргументы для форматирования
   */
  @Override
  public Object[] args() {
    return args.clone();
  }

  public static @NonNull MessageKey of(@NonNull String key, Object... args) {
    return new MessageKey(key, args);
  }
}
