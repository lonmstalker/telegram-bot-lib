package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.plugin.spi.ConfigSection;
import java.util.Map;
import java.util.Optional;

/**
 * Чтение конфигурации из YAML-мэпки.
 */
public class YamlConfigSection implements ConfigSection {
    private final Map<String, Object> values;

    public YamlConfigSection(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Optional<String> get(String key) {
        Object v = values.get(key);
        return v == null ? Optional.empty() : Optional.of(v.toString());
    }
}
