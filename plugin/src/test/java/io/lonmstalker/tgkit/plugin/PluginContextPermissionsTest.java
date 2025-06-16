package io.lonmstalker.tgkit.plugin;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.impl.NoOpTracer;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.state.StateStore;
import io.lonmstalker.tgkit.plugin.spi.BotRegistry;
import java.net.http.HttpClient;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

public class PluginContextPermissionsTest {

    private static class DummyRegistry implements BotRegistry {
        @Override
        public @NonNull Collection<Bot> all() { return java.util.List.of(); }
        @Override
        public @NonNull Optional<Bot> get(@NonNull String id) { return Optional.empty(); }
    }

    private static class NoOpMetrics implements MetricsCollector {
        @Override
        public io.micrometer.core.instrument.@NonNull MeterRegistry registry() { return null; }
        @Override
        public io.micrometer.core.instrument.@NonNull Timer timer(@NonNull String name, io.lonmstalker.observability.@NonNull Tags tags) { return null; }
        @Override
        public io.micrometer.core.instrument.@NonNull Counter counter(@NonNull String name, io.lonmstalker.observability.@NonNull Tags tags) { return null; }
    }

    @Test
    public void denyBotControl() {
        PluginPermissions perms = new PluginPermissions();
        PluginContextImpl ctx = new PluginContextImpl(
                new DummyRegistry(),
                new SimpleEventBus(),
                new InMemoryStateStore(),
                new NoOpMetrics(),
                new NoOpTracer(),
                new ConcurrentHashMap<>(),
                perms,
                HttpClient.newHttpClient());
        assertThrows(SecurityException.class, ctx::bots);
        perms.setBotControl(true);
        assertDoesNotThrow(ctx::bots);
    }

    @Test
    public void readOnlyStore() {
        PluginPermissions perms = new PluginPermissions();
        perms.setStore(PluginPermissions.Store.READ_ONLY);
        PluginContextImpl ctx = new PluginContextImpl(
                new DummyRegistry(),
                new SimpleEventBus(),
                new InMemoryStateStore(),
                new NoOpMetrics(),
                new NoOpTracer(),
                new ConcurrentHashMap<>(),
                perms,
                HttpClient.newHttpClient());
        StateStore store = ctx.defaultStore();
        store.get("test");
        assertThrows(SecurityException.class, () -> store.set("a", "b"));
    }
}
