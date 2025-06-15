package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import io.lonmstalker.tgkit.plugin.spi.Plugin;

/**
 * Менеджер плагинов: загрузка, выгрузка и перезагрузка JAR-файлов.
 */
public class PluginManager {
    private final Path pluginsDir;
    private final EventBus eventBus;
    private final Map<String, LoadedPlugin> plugins = new HashMap<>();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * @param pluginsDir каталог с JAR-плагинами
     * @param eventBus   шина событий
     */
    public PluginManager(Path pluginsDir, EventBus eventBus) {
        this.pluginsDir = pluginsDir;
        this.eventBus = eventBus;
    }

    /**
     * Загружает все JAR-файлы из каталога.
     */
    public void loadAll() throws Exception {
        if (!Files.isDirectory(pluginsDir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pluginsDir, "*.jar")) {
            for (Path jar : stream) {
                load(jar);
            }
        }
    }

    /**
     * Загружает один плагин из файла.
     *
     * @param jarPath путь к JAR
     */
    public void load(Path jarPath) throws Exception {
        URLClassLoader loader = new URLClassLoader(new URL[] {jarPath.toUri().toURL()});
        ServiceLoader<Plugin> sl = ServiceLoader.load(Plugin.class, loader);
        Iterator<Plugin> it = sl.iterator();
        if (!it.hasNext()) {
            loader.close();
            throw new IllegalArgumentException("No Plugin implementation found in " + jarPath);
        }
        Plugin plugin = it.next();
        PluginManifest manifest = readManifest(loader);
        plugin.init(new DefaultPluginContext(eventBus));
        plugin.start();
        plugins.put(manifest.getId(), new LoadedPlugin(plugin, manifest, loader, jarPath));
    }

    private PluginManifest readManifest(ClassLoader loader) throws IOException {
        try (InputStream is = loader.getResourceAsStream("tgkit-plugin.yaml")) {
            if (is == null) {
                throw new IOException("tgkit-plugin.yaml not found");
            }
            return mapper.readValue(is, PluginManifest.class);
        }
    }

    /**
     * Перезагружает плагин.
     *
     * @param id идентификатор плагина
     */
    public void reload(String id) throws Exception {
        LoadedPlugin lp = plugins.get(id);
        if (lp == null) {
            return;
        }
        Path jar = lp.jarPath;
        unload(id);
        load(jar);
    }

    /**
     * Выгружает плагин.
     *
     * @param id идентификатор
     */
    public void unload(String id) throws Exception {
        LoadedPlugin lp = plugins.remove(id);
        if (lp != null) {
            lp.plugin.stop();
            lp.loader.close();
        }
    }

    /**
     * @param id идентификатор плагина
     * @return манифест или {@code null}, если не найден
     */
    public PluginManifest getManifest(String id) {
        LoadedPlugin lp = plugins.get(id);
        if (lp == null) {
            return null;
        }
        return lp.manifest;
    }

    private record LoadedPlugin(Plugin plugin, PluginManifest manifest, URLClassLoader loader, Path jarPath) {}
}
