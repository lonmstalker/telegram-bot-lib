/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.plugin;

import static io.lonmstalker.tgkit.plugin.BotPluginConstants.CURRENT_VERSION;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.zafarkhaja.semver.Version;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.plugin.sort.TopoSorter;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.audit.AuditEvent;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
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
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarFile;
import java.util.zip.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Менеджер плагинов: загрузка, перезагрузка, выгрузка с учётом зависимостей и graceful shutdown.
 */
public final class BotPluginManager implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(BotPluginManager.class);
  private static final MessageDigest MESSAGE_DIGEST;
  private static final long SHUTDOWN_TIMEOUT_MS = 500;
  private static final Version SUPPORTED_API_VERSION =
      Version.valueOf(String.format("%.1f.0", CURRENT_VERSION));

  static {
    try {
      MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new BotApiException(e);
    }
  }

  private final Lock lock = new ReentrantLock();
  private final ObjectMapper yaml = new ObjectMapper(new YAMLFactory());
  private final ExecutorService executor =
      Executors.newSingleThreadExecutor(r -> new Thread(r, "plugin-manager"));
  private final AuditBus auditBus = BotSecurityGlobalConfig.INSTANCE.audit().bus();
  private final Map<String, BotPluginContainer> plugins = new ConcurrentHashMap<>();

  /**
   * Сканирует папку, сортирует плагины по зависимостям и загружает их. Ошибки отдельных JAR
   * логируются, основной процесс не прерывается.
   *
   * @param dir каталог с .jar плагинами
   */
  public void loadAll(Path dir) {
    lock.lock();
    try {
      log.info("Scanning plugin directory: {}", dir.toAbsolutePath());
      if (!Files.isDirectory(dir)) {
        log.debug("{} is not a directory", dir);
        return;
      }

      // 1) Сбор дескрипторов
      Map<String, Path> jarPaths = new HashMap<>();
      List<BotPluginDescriptor> descriptors = new ArrayList<>();
      try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir, "*.jar")) {
        for (Path jar : ds) {
          try {
            BotPluginDescriptor desc = parseDescriptor(jar);
            jarPaths.put(desc.id(), jar);
            descriptors.add(desc);
          } catch (ZipException ze) {
            String name = jar.getFileName().toString();
            log.warn("Invalid JAR {}: {}", name, ze.getMessage());
            auditBus.publish(
                AuditEvent.securityAlert("plugin-scan", "Invalid JAR format: " + name));
          } catch (Exception ex) {
            String name = jar.getFileName().toString();
            log.warn("Error parsing {}: {}", name, ex.getMessage());
            auditBus.publish(AuditEvent.securityAlert("plugin-scan", ex.getMessage()));
          }
        }
      }

      // 2) Топологическая сортировка
      List<BotPluginDescriptor> sorted =
          TopoSorter.sort(descriptors, BotPluginDescriptor::id, BotPluginDescriptor::requires);

      // 3) Загрузка в порядке topo
      for (BotPluginDescriptor desc : sorted) {
        Path jar = jarPaths.get(desc.id());
        try {
          loadPlugin(desc, jar);
        } catch (PluginException pe) {
          log.warn("Skipping plugin {}: {}", desc.id(), pe.getMessage());
          auditBus.publish(AuditEvent.securityAlert("plugin-scan", pe.getMessage()));
        }
      }
    } catch (IOException io) {
      log.error("Failed to scan plugin directory {}", dir, io);
      throw new PluginException("Cannot scan dir " + dir, io);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Горячая перезагрузка: выгружаем старый плагин, заново парсим descriptor и грузим.
   *
   * @param id идентификатор плагина
   */
  public void hotReload(String id) {
    lock.lock();
    try {
      BotPluginContainer old = plugins.get(id);
      if (old == null) {
        throw new PluginException("Plugin '" + id + "' not loaded");
      }
      log.info("Hot reloading plugin {}", id);
      Path jar = old.source();
      unload(id);
      BotPluginDescriptor desc = parseDescriptor(jar);
      loadPlugin(desc, jar);
    } catch (IOException io) {
      throw new PluginException("Failed to reload plugin " + id, io);
    } finally {
      lock.unlock();
    }
  }

  /**
   * Останавливает плагин с учётом beforeStop/stop/afterStop/onUnload и таймаутом.
   *
   * @param id идентификатор плагина
   */
  public void unload(String id) {
    lock.lock();
    try {
      BotPluginContainer container = plugins.remove(id);
      if (container == null) {
        return;
      }
      BotPlugin plugin = container.plugin();
      Future<?> future =
          executor.submit(
              () -> {
                try {
                  plugin.beforeStop();
                  plugin.stop();
                  plugin.afterStop();
                  plugin.onUnload();
                } catch (Exception e) {
                  throw new RuntimeException(e);
                }
              });
      try {
        future.get(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS);
      } catch (TimeoutException te) {
        log.warn("Plugin {} shutdown timed out", id);
      }
      closeQuiet(container.classLoader());
      auditBus.publish(
          AuditEvent.securityAlert(
              "plugin-scan", "plugin:" + container.descriptor().id() + " unloaded"));
      log.info("Plugin {} unloaded", id);
    } catch (Exception ex) {
      throw new PluginException("Failed to unload plugin " + id, ex);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void close() {
    lock.lock();
    try {
      for (String id : new ArrayList<>(plugins.keySet())) {
        unload(id);
      }
      executor.shutdown();
    } finally {
      lock.unlock();
    }
  }

  private BotPluginDescriptor parseDescriptor(Path jar) throws IOException {
    try (JarFile jf = new JarFile(jar.toFile())) {
      var entry = jf.getEntry("plugin.yml");
      if (entry == null) {
        throw new PluginException("plugin.yml missing in " + jar.getFileName());
      }
      try (InputStream in = jf.getInputStream(entry)) {
        return yaml.readValue(in, BotPluginDescriptor.class);
      }
    }
  }

  private void loadPlugin(BotPluginDescriptor desc, Path jar) {
    try {
      checkApiCompatibility(desc);
      verifyHash(jar, desc);

      URL[] urls = {jar.toUri().toURL()};
      URLClassLoader cl = new ChildFirstURLClassLoader(urls, getClass().getClassLoader());

      Class<?> main = Class.forName(desc.mainClass(), false, cl);
      BotPlugin plugin = (BotPlugin) main.getDeclaredConstructor().newInstance();

      BotPluginContext context = new BotPluginContextDefault(cl);
      plugin.onLoad(context);
      plugin.start();

      plugins.put(desc.id(), new BotPluginContainer(desc, plugin, cl, jar));
      auditBus.publish(
          AuditEvent.securityAlert(
              "plugin-scan", "plugin:" + desc.id() + " loaded (v" + desc.version() + ")"));
      log.info("Plugin {} v{} started", desc.id(), desc.version());
    } catch (ZipException ze) {
      throw new PluginException("Invalid JAR format: " + jar.getFileName(), ze);
    } catch (PluginException pe) {
      throw pe;
    } catch (Exception e) {
      throw new PluginException("Failed to load plugin " + desc.id(), e);
    }
  }

  private void checkApiCompatibility(BotPluginDescriptor desc) {
    try {
      Version pluginApi = Version.valueOf(normalizeVersion(desc.api()));
      if (pluginApi.greaterThan(SUPPORTED_API_VERSION)) {
        throw new PluginException("Plugin " + desc.id() + " requires API " + desc.api());
      }
    } catch (IllegalArgumentException ex) {
      throw new PluginException("Invalid API version: " + desc.api(), ex);
    }
  }

  private static String normalizeVersion(String version) {
    if (version.chars().filter(ch -> ch == '.').count() == 1) {
      return version + ".0";
    }
    if (!version.contains(".")) {
      return version + ".0.0";
    }
    return version;
  }

  private void verifyHash(Path jar, BotPluginDescriptor desc) throws IOException {
    if (StringUtils.isBlank(desc.sha256())) {
      log.debug("Skip hash check for plugin {}", desc.id());
      return;
    }
    byte[] data = Files.readAllBytes(jar);
    String checksum = bytesToHex(MESSAGE_DIGEST.digest(data));
    if (!checksum.equalsIgnoreCase(desc.sha256())) {
      throw new PluginException("Checksum mismatch for " + jar.getFileName());
    }
  }

  private static void closeQuiet(Closeable c) {
    try {
      c.close();
    } catch (IOException ignored) {
    }
  }

  private static String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder(bytes.length * 2);
    for (byte b : bytes) {
      String hex = Integer.toHexString(b & 0xFF);
      sb.append(hex.length() == 1 ? "0" : "").append(hex);
    }
    return sb.toString();
  }
}
