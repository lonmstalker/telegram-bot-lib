package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tracer;
import io.lonmstalker.tgkit.core.state.StateStore;
import io.lonmstalker.tgkit.plugin.spi.BotRegistry;
import io.lonmstalker.tgkit.plugin.spi.ConfigSection;
import io.lonmstalker.tgkit.plugin.spi.PluginContext;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Базовая реализация {@link PluginContext}.
 */
public final class PluginContextImpl implements PluginContext {

    private final BotRegistry bots;
    private final EventBus bus;
    private final StateStore store;
    private final MetricsCollector metrics;
    private final Tracer tracer;
    private final Map<String, ConfigSection> configs;
    private final PluginPermissions permissions;
    private final HttpClient httpClient;

    public PluginContextImpl(
            @NonNull BotRegistry bots,
            @NonNull EventBus bus,
            @NonNull StateStore store,
            @NonNull MetricsCollector metrics,
            @NonNull Tracer tracer,
            @NonNull Map<String, ConfigSection> configs,
            @NonNull PluginPermissions permissions,
            @NonNull HttpClient httpClient) {
        this.bots = bots;
        this.bus = bus;
        this.store = store;
        this.metrics = metrics;
        this.tracer = tracer;
        this.configs = configs;
        this.permissions = permissions;
        this.httpClient = httpClient;
    }

    @Override
    public void registerConfig(@NonNull String pluginId, @NonNull ConfigSection config) {
        this.configs.put(pluginId, config);
    }

    @Override
    public @NonNull BotRegistry bots() {
        if (!permissions.isBotControl()) {
            throw new SecurityException("Bot control is not allowed");
        }
        return bots;
    }

    @Override
    public @NonNull EventBus bus() {
        return bus;
    }

    @Override
    public @NonNull StateStore defaultStore() {
        return switch (permissions.getStore()) {
            case NONE -> throw new SecurityException("StateStore access denied");
            case READ_ONLY -> new ReadOnlyStateStore(store);
            case READ_WRITE -> store;
        };
    }

    @Override
    public @NonNull MetricsCollector metrics() {
        return metrics;
    }

    @Override
    public @NonNull Tracer tracer() {
        return tracer;
    }

    @Override
    public @NonNull HttpClient httpClient() {
        if (permissions.getNetwork() == PluginPermissions.Network.NONE) {
            throw new SecurityException("Network access denied");
        }
        return httpClient;
    }

    @Override
    public @NonNull InputStream readFile(@NonNull Path path) throws java.io.IOException {
        if (permissions.getFileSystem() == PluginPermissions.FileSystem.NONE
                || permissions.getFileSystem() == PluginPermissions.FileSystem.WRITE) {
            throw new SecurityException("File read access denied");
        }
        return Files.newInputStream(path);
    }

    @Override
    public @NonNull OutputStream writeFile(@NonNull Path path) throws java.io.IOException {
        if (permissions.getFileSystem() == PluginPermissions.FileSystem.NONE
                || permissions.getFileSystem() == PluginPermissions.FileSystem.READ) {
            throw new SecurityException("File write access denied");
        }
        return Files.newOutputStream(path);
    }

    @Override
    public @NonNull Optional<ConfigSection> config(@NonNull String pluginId) {
        return Optional.ofNullable(configs.get(pluginId));
    }
}
