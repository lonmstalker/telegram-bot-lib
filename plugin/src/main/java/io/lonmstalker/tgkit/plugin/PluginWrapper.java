package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.plugin.spi.Plugin;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Пара плагин + его метаданные.
 */
public record PluginWrapper(Plugin plugin, URLClassLoader classLoader, Path path, PluginManifest manifest) {}
