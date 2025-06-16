package io.lonmstalker.tgkit.plugin.spi;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Конфигурационный раздел плагина.
 */
public interface ConfigSection {

    @NonNull Optional<String> get(@NonNull String key);

    default int getInt(String key, int def) {
        return get(key).map(Integer::parseInt).orElse(def);
    }

    default boolean getBoolean(String key, boolean def) {
        return get(key).map(Boolean::parseBoolean).orElse(def);
    }
}
