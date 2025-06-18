package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.audit.AuditEvent;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarFile;

import static io.lonmstalker.tgkit.plugin.BotPluginConstants.CURRENT_VERSION;

/**
 * Менеджер плагинов.<br>
 * Потокобезопасен, не зависит от Spring/Guice, использует чистый JDK 21.
 *
 * <p>Поддерживает:</p>
 * <ul>
 *   <li>загрузку *.jar* из каталога (или URL) — {@link #loadAll(Path)}</li>
 *   <li>горячую перезагрузку — {@link #hotReload(String)}</li>
 *   <li>безопасное выгрузку — {@link #unload(String)}</li>
 * </ul>
 *
 * <p>Каждый плагин изолирован отдельным {@link URLClassLoader} («child-first»),
 *  что упрощает GC после {@code unload()}.</p>
 */
@Slf4j
@Builder
public final class BotPluginManager implements AutoCloseable {
    private static final MessageDigest MESSAGE_DIGEST;

    static {
        try {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new BotApiException(e);
        }
    }

    private final AuditBus auditBus;
    private final BotGlobalConfig cfg;
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    private final Map<String, BotPluginContainer> plugins = new ConcurrentHashMap<>();

    public void loadAll(@NonNull Path dir) {
        log.info("Scanning plugins dir: {}", dir.toAbsolutePath());
        try {
            if (Files.notExists(dir)) {
                log.debug("Plugins dir {} does not exist", dir.toAbsolutePath());
                return;
            }
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.jar")) {
                for (Path jar : ds) {
                    load(jar);
                }
            }
        } catch (Exception e) {
            throw new PluginException("Cannot scan dir " + dir, e);
        }
    }

    public void hotReload(@NonNull String id) {
        BotPluginContainer old = plugins.get(id);
        if (old == null) {
            throw new PluginException("Plugin '" + id + "' not loaded");
        }

        log.info("Hot-reloading plugin {}", id);
        Path source = old.source();

        unload(id);               // ① останавливаем
        load(source);             // ② поднимаем заново
    }

    public void unload(@NonNull String id) {
        BotPluginContainer c = plugins.remove(id);
        if (c == null) {
            return;
        }
        try {
            c.plugin().onUnload();

            c.plugin().stop();
            closeQuiet(c.classLoader());

            auditBus.publish(AuditEvent.securityAlert("plugin:" + id, "unloaded"));
            log.info("Plugin {} unloaded", id);
        } catch (Exception e) {
            throw new PluginException("stop() failed for " + id, e);
        }
    }

    @Override
    public void close() {
        plugins.keySet().forEach(this::unload);
    }

    /* Основная точка загрузки одного JAR */
    private void load(@NonNull Path jar) {
        try {
            BotPluginDescriptor dsc = parseDescriptor(jar);
            checkApiCompatibility(dsc);
            verifyHash(jar, dsc);

            // child-first ClassLoader (delegates to parent last)
            URLClassLoader cl = new ChildFirstURLClassLoader(
                    new URL[]{jar.toUri().toURL()},
                    BotPluginManager.class.getClassLoader()
            );

            Class<?> main = Class.forName(dsc.mainClass(), false, cl);
            BotPlugin plugin = (BotPlugin) main.getDeclaredConstructor().newInstance();

            BotPluginContext ctx = new BotPluginContextDefault(cl);
            plugin.onLoad(ctx);
            plugin.start();

            plugins.put(dsc.id(), new BotPluginContainer(dsc, plugin, cl, jar));
            auditBus.publish(AuditEvent.securityAlert("system",
                    "plugin " + dsc.id() + " loaded (" + dsc.version() + ")"));

            log.info("Plugin {}:{} started", dsc.id(), dsc.version());
        } catch (Exception e) {
            throw new PluginException("Failed to load plugin from " + jar, e);
        }
    }

    private @NonNull BotPluginDescriptor parseDescriptor(@NonNull Path jar) throws Exception {
        try (JarFile jf = new JarFile(jar.toFile())) {
            var entry = jf.getEntry("plugin.yml");
            if (entry == null) {
                throw new PluginException("plugin.yml missing in " + jar);
            }
            try (InputStream in = jf.getInputStream(entry)) {
                return yaml.readValue(in, BotPluginDescriptor.class);
            }
        }
    }

    private void checkApiCompatibility(@NonNull BotPluginDescriptor desc) {
        if (Double.parseDouble(desc.api()) > CURRENT_VERSION) {
            throw new PluginException("Plugin " + desc.id() + " requires API " + desc.api());
        }
    }

    private void verifyHash(@NonNull Path jar,
                            @NonNull BotPluginDescriptor desc) throws Exception {
        if (desc.sha256() == null) {
            return; // skip
        }
        byte[] b = Files.readAllBytes(jar);
        String calc = bytes2hex(MESSAGE_DIGEST.digest(b));
        if (!calc.equalsIgnoreCase(desc.sha256())) {
            throw new PluginException("Checksum mismatch for " + jar.getFileName());
        }
    }

    private static void closeQuiet(Closeable c) {
        try {
            c.close();
        } catch (Exception ignore) {
        }
    }

    private static String bytes2hex(byte[] d) {
        var sb = new StringBuilder();
        for (byte b : d) {
            var hex = Integer.toHexString(b & 0xFF);
            sb.append(hex.length() == 1 ? "0" + hex : hex);
        }
        return sb.toString();
    }
}