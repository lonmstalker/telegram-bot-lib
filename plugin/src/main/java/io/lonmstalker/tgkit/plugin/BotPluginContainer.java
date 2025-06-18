package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * runtime container
 */
record BotPluginContainer(@NonNull BotPluginDescriptor descriptor,
                          @NonNull BotPlugin plugin,
                          @NonNull URLClassLoader classLoader,
                          @NonNull Path source) {
}