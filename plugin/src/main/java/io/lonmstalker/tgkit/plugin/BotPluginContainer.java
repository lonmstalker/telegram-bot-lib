package io.lonmstalker.tgkit.plugin;

import java.net.URLClassLoader;
import java.nio.file.Path;
import org.checkerframework.checker.nullness.qual.NonNull;

/** runtime container */
record BotPluginContainer(
    @NonNull BotPluginDescriptor descriptor,
    @NonNull BotPlugin plugin,
    @NonNull URLClassLoader classLoader,
    @NonNull Path source) {}
