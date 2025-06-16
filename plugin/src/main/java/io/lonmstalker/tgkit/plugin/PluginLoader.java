package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.lonmstalker.tgkit.plugin.spi.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Загрузчик плагинов из директории plugins/.
 */
public final class PluginLoader {
    private final Path pluginsDir = Paths.get("plugins");
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Загружает все плагины из директории {@code plugins}.
     */
    public @NonNull Collection<PluginWrapper> loadAll() throws Exception {
        List<PluginWrapper> list = new ArrayList<>();
        if (!Files.isDirectory(pluginsDir)) {
            return list;
        }
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(pluginsDir, "*.jar")) {
            for (Path jar : ds) {
                URLClassLoader cl = new URLClassLoader(new URL[]{jar.toUri().toURL()}, getClass().getClassLoader());
                PluginManifest manifest = readManifest(cl);
                ServiceLoader<Plugin> sl = ServiceLoader.load(Plugin.class, cl);
                for (Plugin p : sl) {
                    list.add(new PluginWrapper(p, cl, jar, manifest));
                }
            }
        }
        return list;
    }

    /**
     * Считывает манифест плагина из ресурса {@code tgkit-plugin.yaml}.
     */
    private @NonNull PluginManifest readManifest(@NonNull ClassLoader cl) throws Exception {
        try (InputStream is = cl.getResourceAsStream("tgkit-plugin.yaml")) {
            if (is == null) {
                return new PluginManifest();
            }
            return mapper.readValue(is, PluginManifest.class);
        }
    }
}
