package io.lonmstalker.tgkit.plugin;

import static io.lonmstalker.tgkit.plugin.BotPluginConstants.CURRENT_VERSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.audit.AuditEvent;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

/**
 * Unit-тесты для BotPluginManager. Все ошибки теперь обрабатываются внутри loadAll() с публикацией
 * в AuditBus, исключения наружу не выбрасываются.
 */
public class BotPluginManagerTest {

  @TempDir Path tempDir;
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
    createPluginJar(
        jar,
        "test-id",
        CURRENT_VERSION.toString(),
        CURRENT_VERSION.toString(),
        TestPlugin.class.getName(),
        null);

    // загрузка не должна падать
    manager.loadAll(tempDir);

    // проверяем публикацию события о загрузке
    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction()
                            .equals("plugin:test-id loaded (v" + CURRENT_VERSION + ")")));

    // выгрузка
    Mockito.reset(auditBus);
    manager.unload("test-id");
    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction().equals("plugin:test-id unloaded")));
  }

  @Test
  void testInvalidJarFormat() throws Exception {
    // создаём файл, который не является JAR
    Path jar = tempDir.resolve("bad.jar");
    Files.write(jar, new byte[] {0, 1, 2});

    manager.loadAll(tempDir);

    // проверяем публикацию события об ошибке формата
    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction().contains("Invalid JAR format: bad.jar")));
  }

  @Test
  void testApiCompatibility() throws Exception {
    // плагин с несовместимым api
    Path jar = tempDir.resolve("ver.jar");
    createPluginJar(jar, "id", CURRENT_VERSION.toString(), "9.9", TestPlugin.class.getName(), null);

    manager.loadAll(tempDir);

    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction().contains("requires API")));
  }

  @Test
  void testHashMismatch() throws Exception {
    // плагин с неправильным sha256
    Path jar = tempDir.resolve("hash.jar");
    createPluginJar(
        jar,
        "id2",
        CURRENT_VERSION.toString(),
        CURRENT_VERSION.toString(),
        TestPlugin.class.getName(),
        "deadbeef");

    manager.loadAll(tempDir);

    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction().contains("Checksum mismatch")));
  }

  @Test
  void testExecutorThreadName() throws Exception {
    Path jar = tempDir.resolve("thread.jar");
    createPluginJar(
        jar,
        "thread",
        CURRENT_VERSION.toString(),
        CURRENT_VERSION.toString(),
        ThreadNamePlugin.class.getName(),
        null);

    manager.loadAll(tempDir);
    manager.unload("thread");

    assertEquals(
        "plugin-manager", ThreadNamePlugin.thread, "Имя потока должно совпадать с factory value");
  }

  @Test
  void testHotReload() throws Exception {
    Path jar = tempDir.resolve("reload.jar");
    createPluginJar(
        jar, "reload", "1.0", CURRENT_VERSION.toString(), ReloadPluginV1.class.getName(), null);

    manager.loadAll(tempDir);
    assertEquals("v1", System.getProperty("reload.phase"));

    Mockito.reset(auditBus);
    createPluginJar(
        jar, "reload", "2.0", CURRENT_VERSION.toString(), ReloadPluginV2.class.getName(), null);

    manager.hotReload("reload");

    assertEquals("v2", System.getProperty("reload.phase"));
    verify(auditBus).publish(argThat(evt -> evt.getAction().equals("plugin:reload unloaded")));
    verify(auditBus).publish(argThat(evt -> evt.getAction().equals("plugin:reload loaded (v2.0)")));
  }

  @Test
  void testUnloadTimeout() throws Exception {
    Path jar = tempDir.resolve("slow.jar");
    createPluginJar(
        jar,
        "slow",
        CURRENT_VERSION.toString(),
        CURRENT_VERSION.toString(),
        SlowPlugin.class.getName(),
        null);

    manager.loadAll(tempDir);
    Mockito.reset(auditBus);

    long start = System.currentTimeMillis();
    manager.unload("slow");
    long elapsed = System.currentTimeMillis() - start;

    assertTrue(elapsed < 700, "Выгрузка не должна ждать завершения stop()");

    // ожидаем завершения onUnload в фоне
    assertTrue(
        SlowPlugin.unloaded.await(2, TimeUnit.SECONDS),
        "onUnload должен завершиться, даже если произошёл таймаут");

    verify(auditBus).publish(argThat(evt -> evt.getAction().equals("plugin:slow unloaded")));
  }

  @Test
  void testMissingDescriptor() throws Exception {
    Path jar = tempDir.resolve("missing.jar");
    try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar.toFile()))) {
      jos.putNextEntry(new JarEntry("dummy.txt"));
      jos.write(new byte[] {1, 2});
      jos.closeEntry();
    }

    manager.loadAll(tempDir);

    verify(auditBus).publish(argThat(evt -> evt.getAction().contains("plugin.yml missing")));
  }

  private static void createPluginJar(
      Path path, String id, String version, String api, String mainClass, String sha256)
      throws Exception {
    ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
    BotPluginDescriptor desc =
        BotPluginDescriptor.builder()
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
    public void onLoad(@NonNull BotPluginContext context) {}

    @Override
    public void start() {}

    @Override
    public void stop() {}

    @Override
    public void onUnload() {}
  }

  public static class ThreadNamePlugin implements BotPlugin {
    static volatile String thread;

    @Override
    public void stop() {
      thread = Thread.currentThread().getName();
    }
  }

  public static class ReloadPluginV1 implements BotPlugin {
    @Override
    public void start() {
      System.setProperty("reload.phase", "v1");
    }
  }

  public static class ReloadPluginV2 implements BotPlugin {
    @Override
    public void start() {
      System.setProperty("reload.phase", "v2");
    }
  }

  public static class SlowPlugin implements BotPlugin {
    static final CountDownLatch unloaded = new CountDownLatch(1);

    @Override
    public void stop() throws Exception {
      Thread.sleep(600);
    }

    @Override
    public void onUnload() {
      unloaded.countDown();
    }
  }
}
