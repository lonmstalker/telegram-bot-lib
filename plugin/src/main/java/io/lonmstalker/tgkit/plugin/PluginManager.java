package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tracer;
import io.lonmstalker.tgkit.core.state.StateStore;
import io.lonmstalker.tgkit.plugin.spi.BotRegistry;
import io.lonmstalker.tgkit.plugin.spi.Plugin;
import io.lonmstalker.tgkit.plugin.spi.EventBus;
import io.lonmstalker.tgkit.plugin.spi.ConfigSection;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import io.lonmstalker.tgkit.plugin.YamlConfigSection;

/**
 * Управление жизненным циклом плагинов.
 */
public final class PluginManager {

    private final ConcurrentMap<String, PluginWrapper> active = new ConcurrentHashMap<>();
    private final Map<String, ConfigSection> configs = new ConcurrentHashMap<>();

    private final BotRegistry bots;
    private final EventBus bus;
    private final StateStore store;
    private final MetricsCollector metrics;
    private final Tracer tracer;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public PluginManager(BotRegistry bots, EventBus bus, StateStore store,
                         MetricsCollector metrics, Tracer tracer) {
        this.bots = bots;
        this.bus = bus;
        this.store = store;
        this.metrics = metrics;
        this.tracer = tracer;
    }

    public void bootstrap() throws Exception {
        Collection<PluginWrapper> plugins = new PluginLoader().loadAll();
        var sorted = new ArrayList<>(plugins);
        sorted.sort(Comparator.comparingInt(p -> p.manifest().order()));
        for (PluginWrapper w : sorted) {
            PluginContextImpl ctx = new PluginContextImpl(
                    bots,
                    bus,
                    store,
                    metrics,
                    tracer,
                    configs,
                    w.manifest().getPermissions(),
                    httpClient);
            if (!w.manifest().getConfig().isEmpty()) {
                ctx.registerConfig(w.manifest().getId(), new YamlConfigSection(w.manifest().getConfig()));
            }
            w.plugin().init(ctx);
            w.plugin().start();
            active.put(w.manifest().getId(), w);
        }
    }

    public void stopAll() {
        for (PluginWrapper w : active.values()) {
            try {
                w.plugin().stop();
                w.classLoader().close();
            } catch (Exception ignored) {
            }
        }
        active.clear();
    }
}
