package io.lonmstalker.tgkit.plugin.spi;

import java.util.Optional;

/**
 * Конфигурационный раздел плагина.
 */
public interface ConfigSection {

    Optional<String> get(String key);

    default int getInt(String key, int def) {
        return get(key).map(Integer::parseInt).orElse(def);
    }

    default boolean getBoolean(String key, boolean def) {
        return get(key).map(Boolean::parseBoolean).orElse(def);
    }
}
