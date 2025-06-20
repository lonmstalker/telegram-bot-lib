package io.lonmstalker.tgkit.plugin;

import com.github.zafarkhaja.semver.Version;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
/**
 * Набор констант, используемых системой плагинов.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * if (descriptor.api() > BotPluginConstants.CURRENT_VERSION) {
 *     throw new UnsupportedOperationException();
 * }
 * }</pre>
 */
public class BotPluginConstants {
  /** Текущая поддерживаемая версия API плагинов. */
  public static final Version CURRENT_VERSION = resolveCurrentVersion();

  private static Version resolveCurrentVersion() {
    String raw = BotPluginConstants.class.getPackage().getImplementationVersion();
    if (raw == null) {
      raw = "0.0.0";
    }
    raw = raw.replaceFirst("-.*$", "");
    return Version.valueOf(normalizeVersion(raw));
  }

  private static String normalizeVersion(String version) {
    long dots = version.chars().filter(ch -> ch == '.').count();
    if (dots == 1) {
      return version + ".0";
    }
    if (dots == 0) {
      return version + ".0.0";
    }
    return version;
  }
}
