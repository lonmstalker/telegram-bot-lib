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
    public DefaultPluginContext(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public BotRegistry bots() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EventBus bus() {
        return eventBus;
    }

    @Override
    public StateStore defaultStore() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpClient httpClient() {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream readFile(Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public OutputStream writeFile(Path path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetricsCollector metrics() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Tracer tracer() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ConfigSection> config(String pluginId) {
        return Optional.empty();
    }

    @Override
    public void registerConfig(String pluginId, ConfigSection config) {
        // nothing
    }
}
