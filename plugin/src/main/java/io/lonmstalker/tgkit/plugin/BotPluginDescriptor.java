package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

record BotPluginDescriptor(@NonNull String id,
                           @NonNull String name,
                           @NonNull String version,
                           @NonNull String api,
                           @NonNull String mainClass,
                           @Nullable String sha256,
                           @NonNull List<@NonNull String> requires) {
}
