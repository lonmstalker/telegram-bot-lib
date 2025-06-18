package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.audit.AuditEvent;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.IOException;
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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarFile;
import java.util.zip.ZipException;

import static io.lonmstalker.tgkit.plugin.BotPluginConstants.CURRENT_VERSION;

/**
 * Менеджер плагинов: загрузка, перезагрузка, выгрузка.
 */
@Slf4j
@ThreadSafe
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
    private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    private final Map<String, BotPluginContainer> plugins = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    /**
     * Создаёт менеджер с дефолтной конфигурацией.
     */
    public BotPluginManager() {
        this.auditBus = BotSecurityGlobalConfig.INSTANCE.audit().bus();
    }

    /**
     * Сканирует каталог и загружает все JAR-плагины.
     * Блокировка гарантирует потокобезопасность без дедлоков.
     */
    public void loadAll(@NonNull Path dir) {
        lock.lock();
        try {
            log.info("Scanning plugins dir: {}", dir.toAbsolutePath());
            if (!Files.isDirectory(dir)) {
                log.warn("Plugins dir {} not found or not a directory", dir);
                return;
            }
            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.jar")) {
                for (Path jar : ds) {
                    try {
                        loadPlugin(jar);
                    } catch (PluginException pe) {
                        log.warn("Skipping plugin {}: {}", jar.getFileName(), pe.getMessage());
                        auditBus.publish(AuditEvent.securityAlert("plugin-scan",
                                "failed load " + jar.getFileName() + ": " + pe.getMessage()));
                    }
                }
            }
        } catch (Exception e) {
            throw new PluginException("Cannot scan dir " + dir, e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Горячая перезагрузка плагина по id: сначала unload, затем load.
     */
    public void hotReload(@NonNull String id) {
        lock.lock();
        try {
            var old = plugins.get(id);
            if (old == null) {
                throw new PluginException("Plugin '" + id + "' not loaded");
            }
            log.info("Hot-reloading plugin {}", id);
            Path source = old.source();
            unload(id);
            loadPlugin(source);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Останавливает и выгружает плагин по id.
     */
    public void unload(@NonNull String id) {
        lock.lock();
        try {
            BotPluginContainer container = plugins.remove(id);
            if (container == null) {
                return;
            }
            container.plugin().onUnload();
            container.plugin().stop();
            closeQuiet(container.classLoader());
            auditBus.publish(AuditEvent.securityAlert("plugin-scan", "plugin:" + id + " unloaded"));
            log.info("Plugin {} unloaded", id);
        } catch (Exception e) {
            throw new PluginException("stop() failed for " + id, e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Выгружает все плагины.
     */
    @Override
    public void close() {
        lock.lock();
        try {
            for (String id : plugins.keySet()) {
                unload(id);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Основная точка загрузки одного JAR.
     */
    private void loadPlugin(@NonNull Path jar) {
        try {
            BotPluginDescriptor desc = parseDescriptor(jar);
            checkApiCompatibility(desc);
            verifyHash(jar, desc);

            URLClassLoader classLoader = new ChildFirstURLClassLoader(
                    new URL[]{jar.toUri().toURL()},
                    BotPluginManager.class.getClassLoader()
            );

            Class<?> mainClass = Class.forName(desc.mainClass(), false, classLoader);
            BotPlugin plugin = (BotPlugin) mainClass.getDeclaredConstructor().newInstance();

            BotPluginContext context = new BotPluginContextDefault(classLoader);
            plugin.onLoad(context);
            plugin.start();

            plugins.put(desc.id(), new BotPluginContainer(desc, plugin, classLoader, jar));
            auditBus.publish(AuditEvent.securityAlert("plugin-scan",
                    "plugin:" + desc.id() + " loaded (v" + desc.version() + ")"));
            log.info("Plugin {}:{} started", desc.id(), desc.version());
        } catch (ZipException ze) {
            throw new PluginException("Invalid JAR format: " + jar.getFileName(), ze);
        } catch (PluginException pe) {
            throw pe;
        } catch (Exception e) {
            throw new PluginException("Failed to load plugin from " + jar.getFileName(), e);
        }
    }

    // парсинг plugin.yml, ZipException прокидывается
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

    private void verifyHash(@NonNull Path jar, @NonNull BotPluginDescriptor desc) throws Exception {
        if (desc.sha256() == null) {
            return;
        }
        byte[] data = Files.readAllBytes(jar);
        String checksum = bytes2hex(MESSAGE_DIGEST.digest(data));
        if (!checksum.equalsIgnoreCase(desc.sha256())) {
            throw new PluginException("Checksum mismatch for " + jar.getFileName());
        }
    }

    private static void closeQuiet(Closeable c) {
        try {
            c.close();
        } catch (IOException ignore) {
        }
    }

    private static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String h = Integer.toHexString(b & 0xFF);
            sb.append(h.length() == 1 ? "0" + h : h);
        }
        return sb.toString();
    }
}
