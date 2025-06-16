package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tracer;
import io.lonmstalker.tgkit.core.state.StateStore;
import io.lonmstalker.tgkit.plugin.spi.BotRegistry;
import io.lonmstalker.tgkit.plugin.spi.ConfigSection;
import io.lonmstalker.tgkit.plugin.spi.PluginContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Минимальная реализация {@link PluginContext}: поддерживает только шину
 * событий.
 */
public class DefaultPluginContext implements PluginContext {
    private final EventBus eventBus;

    /**
     * @param eventBus шина событий
     */
    public DefaultPluginContext(@NonNull EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public @NonNull BotRegistry bots() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull EventBus bus() {
        return eventBus;
    }

    @Override
    public @NonNull StateStore defaultStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull HttpClient httpClient() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull InputStream readFile(@NonNull Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull OutputStream writeFile(@NonNull Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull MetricsCollector metrics() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Tracer tracer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Optional<ConfigSection> config(@NonNull String pluginId) {
        return Optional.empty();
    }

    @Override
    public void registerConfig(@NonNull String pluginId, @NonNull ConfigSection config) {
        // nothing
    }
}
