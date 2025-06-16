package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.plugin.spi.ConfigSection;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Чтение конфигурации из YAML-мэпки.
 */
public class YamlConfigSection implements ConfigSection {
    private final Map<String, Object> values;

    public YamlConfigSection(@NonNull Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public @NonNull Optional<String> get(@NonNull String key) {
        Object v = values.get(key);
        return v == null ? Optional.empty() : Optional.of(v.toString());
    }
}
