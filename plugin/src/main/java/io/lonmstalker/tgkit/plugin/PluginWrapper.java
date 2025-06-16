package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import io.lonmstalker.tgkit.plugin.spi.Plugin;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Пара плагин + его метаданные.
 */
public record PluginWrapper(
        @NonNull Plugin plugin,
        @NonNull URLClassLoader classLoader,
        @NonNull Path path,
        @NonNull PluginManifest manifest) {}
