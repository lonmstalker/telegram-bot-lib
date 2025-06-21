/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.plugin;

import static io.github.tgkit.plugin.internal.BotPluginConstants.CURRENT_VERSION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.tgkit.plugin.internal.BotPluginDescriptor;
import io.github.tgkit.security.audit.AuditBus;
import io.github.tgkit.security.audit.AuditEvent;
import io.github.tgkit.security.config.BotSecurityGlobalConfig;
import io.github.tgkit.security.init.BotSecurityInitializer;
import io.github.tgkit.testkit.TestBotBootstrap;
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

  static {
    TestBotBootstrap.initOnce();
    BotSecurityInitializer.init();
  }

  @TempDir Path tempDir;
  private AuditBus auditBus;
  private BotPluginManager manager;

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
        String.valueOf(CURRENT_VERSION),
        String.valueOf(CURRENT_VERSION),
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
  void testApiVersionAboveSupported() throws Exception {
    // плагин требует более новую версию API
    Path jar = tempDir.resolve("ver.jar");
    createPluginJar(
        jar, "id", String.valueOf(CURRENT_VERSION), "1.0.0", TestPlugin.class.getName(), null);

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
        String.valueOf(CURRENT_VERSION),
        String.valueOf(CURRENT_VERSION),
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
  void testApiInvalidFormat() throws Exception {
    Path jar = tempDir.resolve("badapi.jar");
    createPluginJar(
        jar, "bad", String.valueOf(CURRENT_VERSION), "one.two", TestPlugin.class.getName(), null);

    manager.loadAll(tempDir);

    verify(auditBus)
        .publish(
            argThat(
                (AuditEvent evt) ->
                    "plugin-scan".equals(evt.getActor())
                        && evt.getAction().contains("Invalid API version")));
  }

  @Test
  void testExecutorThreadName() throws Exception {
    Path jar = tempDir.resolve("thread.jar");
    createPluginJar(
        jar,
        "thread",
        String.valueOf(CURRENT_VERSION),
        String.valueOf(CURRENT_VERSION),
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
        jar,
        "reload",
        "1.0",
        String.valueOf(CURRENT_VERSION),
        ReloadPluginV1.class.getName(),
        null);

    manager.loadAll(tempDir);
    assertEquals("v1", System.getProperty("reload.phase"));

    Mockito.reset(auditBus);
    createPluginJar(
        jar,
        "reload",
        "2.0",
        String.valueOf(CURRENT_VERSION),
        ReloadPluginV2.class.getName(),
        null);

    manager.hotReload("reload");

    assertEquals("v2", System.getProperty("reload.phase"));
    verify(auditBus).publish(argThat(evt -> evt.getAction().equals("plugin:reload unloaded")));
    verify(auditBus).publish(argThat(evt -> evt.getAction().equals("plugin:reload loaded (v2.0)")));
  }

  @Test
  void testHotReloadDescriptorFailure() throws Exception {
    Path jar = tempDir.resolve("fail.jar");
    createPluginJar(
        jar, "fail", "1.0", String.valueOf(CURRENT_VERSION), ReloadPluginV1.class.getName(), null);

    manager.loadAll(tempDir);
    assertEquals("v1", System.getProperty("reload.phase"));

    Mockito.reset(auditBus);
    try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(jar.toFile()))) {
      jos.putNextEntry(new JarEntry("dummy.txt"));
      jos.write(new byte[] {1, 2});
      jos.closeEntry();
    }

    manager.hotReload("fail");

    assertEquals("v1", System.getProperty("reload.phase"));
    verify(auditBus).publish(argThat(evt -> evt.getAction().contains("plugin.yml missing")));
    verify(auditBus, never())
        .publish(argThat(evt -> evt.getAction().equals("plugin:fail unloaded")));
    verify(auditBus, never())
        .publish(argThat(evt -> evt.getAction().contains("plugin:fail loaded")));
  }

  @Test
  void testUnloadTimeout() throws Exception {
    Path jar = tempDir.resolve("slow.jar");
    createPluginJar(
        jar,
        "slow",
        String.valueOf(CURRENT_VERSION),
        String.valueOf(CURRENT_VERSION),
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

  @Test
  void testDescriptorBuilderMissingRequiredField() {
    assertThrows(
        IllegalStateException.class,
        () ->
            BotPluginDescriptor.builder()
                .id("test")
                .version("1.0")
                // api not set
                .mainClass("main")
                .minCoreVersion("1.0")
                .build());
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
