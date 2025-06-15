package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.Map;
import lombok.Data;

/**
 * Манифест плагина, считываемый из tgkit-plugin.yaml внутри JAR.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PluginManifest {
    private String id;
    private String name;
    private String version;
    private int order = 0;
    private Map<String, Object> config = Collections.emptyMap();
    private PluginPermissions permissions = new PluginPermissions();
}
