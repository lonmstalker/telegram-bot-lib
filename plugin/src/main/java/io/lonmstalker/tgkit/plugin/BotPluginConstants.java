package io.lonmstalker.tgkit.plugin;

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
  private BotPluginConstants() {}

  /** Текущая поддерживаемая версия API плагинов. */
  public static final Double CURRENT_VERSION = 0.1;
}
