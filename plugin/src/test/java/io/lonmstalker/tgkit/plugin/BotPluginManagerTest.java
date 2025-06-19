package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.audit.AuditEvent;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static io.lonmstalker.tgkit.plugin.BotPluginConstants.CURRENT_VERSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Unit-тесты для BotPluginManager.
 * Все ошибки теперь обрабатываются внутри loadAll() с публикацией в AuditBus,
 * исключения наружу не выбрасываются.
 */
public class BotPluginManagerTest {

    @TempDir
    Path tempDir;
    private AuditBus auditBus;
    private BotPluginManager manager;

    static {
        BotCoreInitializer.init();
        BotSecurityInitializer.init();
    }

    @BeforeEach
    void setUp() {
        auditBus = mock(AuditBus.class);
        BotSecurityGlobalConfig.INSTANCE.audit().bus(auditBus);
        manager = new BotPluginManager();
    }

    @Test
    void testLoadAndUnloadPlugin() throws Exception {
        // подготовка рабочего плагина
        Path jar = tempDir.resolve("test-plugin.jar");
        createPluginJar(jar,
                "test-id",
                String.valueOf(CURRENT_VERSION),
                String.valueOf(CURRENT_VERSION),
                TestPlugin.class.getName(),
                null);

        // загрузка не должна падать
        manager.loadAll(tempDir);

        // проверяем публикацию события о загрузке
        verify(auditBus).publish(argThat((AuditEvent evt) ->
                "plugin-scan".equals(evt.getActor()) &&
                        evt.getAction().equals("plugin:test-id loaded (v" + CURRENT_VERSION + ")")
        ));

        // выгрузка
        Mockito.reset(auditBus);
        manager.unload("test-id");
        verify(auditBus).publish(argThat((AuditEvent evt) ->
                "plugin-scan".equals(evt.getActor()) &&
                        evt.getAction().equals("plugin:test-id unloaded")
        ));
    }

    @Test
    void testInvalidJarFormat() throws Exception {
        // создаём файл, который не является JAR
        Path jar = tempDir.resolve("bad.jar");
        Files.write(jar, new byte[]{0, 1, 2});

        manager.loadAll(tempDir);

        // проверяем публикацию события об ошибке формата
        verify(auditBus).publish(argThat((AuditEvent evt) ->
                "plugin-scan".equals(evt.getActor()) &&
                        evt.getAction().contains("Invalid JAR format: bad.jar")
        ));
    }

    @Test
    void testApiCompatibility() throws Exception {
        // плагин с несовместимым api
        Path jar = tempDir.resolve("ver.jar");
        createPluginJar(jar,
                "id",
                String.valueOf(CURRENT_VERSION),
                "9.9",
                TestPlugin.class.getName(),
                null);

        manager.loadAll(tempDir);

        verify(auditBus).publish(argThat((AuditEvent evt) ->
                "plugin-scan".equals(evt.getActor()) &&
                        evt.getAction().contains("requires API")
        ));
    }

    @Test
    void testHashMismatch() throws Exception {
        // плагин с неправильным sha256
        Path jar = tempDir.resolve("hash.jar");
        createPluginJar(jar,
                "id2",
                String.valueOf(CURRENT_VERSION),
                String.valueOf(CURRENT_VERSION),
                TestPlugin.class.getName(),
                "deadbeef");

        manager.loadAll(tempDir);

        verify(auditBus).publish(argThat((AuditEvent evt) ->
                "plugin-scan".equals(evt.getActor()) &&
                        evt.getAction().contains("Checksum mismatch")
        ));
    }

    @Test
    void testExecutorThreadName() throws Exception {
        Path jar = tempDir.resolve("thread.jar");
        createPluginJar(jar,
                "thread",
                String.valueOf(CURRENT_VERSION),
                String.valueOf(CURRENT_VERSION),
                ThreadNamePlugin.class.getName(),
                null);

        manager.loadAll(tempDir);
        manager.unload("thread");

        assertEquals("plugin-manager", ThreadNamePlugin.thread,
                "Имя потока должно совпадать с factory value");
    }

    private static void createPluginJar(Path path,
                                        String id,
                                        String version,
                                        String api,
                                        String mainClass,
                                        String sha256) throws Exception {
        ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
        BotPluginDescriptor desc = BotPluginDescriptor.builder()
                .id(id)
                .version(version)
                .api(api)
                .mainClass(mainClass)
                .sha256(sha256)
                .requires(List.of())
                .build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yaml.writeValue(baos, desc);

        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(path.toFile()))) {
            jos.putNextEntry(new JarEntry("plugin.yml"));
            jos.write(baos.toByteArray());
            jos.closeEntry();
        }
    }

    public static class TestPlugin implements BotPlugin {
        @Override
        public void onLoad(@NonNull BotPluginContext context) {
        }

        @Override
        public void start() {
        }

        @Override
        public void stop() {
        }

        @Override
        public void onUnload() {
        }
    }

    public static class ThreadNamePlugin implements BotPlugin {
        static volatile String thread;

        @Override
        public void stop() {
            thread = Thread.currentThread().getName();
        }
    }
}
