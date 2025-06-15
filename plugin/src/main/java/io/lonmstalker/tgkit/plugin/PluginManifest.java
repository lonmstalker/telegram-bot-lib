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
    /** Уникальный идентификатор плагина. */
    private String id;
    /** Отображаемое имя плагина. */
    private String name;
    /** Версия плагина. */
    private String version;
    /** Порядок загрузки. */
    private int order = 0;
    /** Дополнительная конфигурация. */
    private Map<String, Object> config = Collections.emptyMap();
    /** Разрешения плагина. */
    private PluginPermissions permissions = new PluginPermissions();
}
