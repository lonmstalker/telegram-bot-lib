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
package io.lonmstalker.tgkit.core.resource;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.jar.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

@DisplayName("ResourceLoader – integration smoke-tests")
class ResourceLoaderTest {

  /* static yaml/json тело – проверяем целостность ↓ */
  private static final String YAML = "name: test\nvalue: 42\n";
  private static final String JSON = "{\"hello\":\"world\"}";

  @TempDir Path tmp;

  static {
    TestBotBootstrap.initOnce();
  }

  /* ───────────────── classpath loader ──────────────────────────── */
  @Test
  void classpathLoader() throws Exception {
    // /test.yml лежит рядом с этим тестом (src/test/resources)
    var cp = Loaders.classpath("/test.yml");
    String s = cp.text();
    assertThat(s).contains("test.yml-ok"); // см. test-resource
    assertThat(cp.id()).isEqualTo("cp:/test.yml");
  }

  /* ───────────────── file loader ───────────────────────────────── */
  @Test
  void fileLoader() throws Exception {
    Path f = tmp.resolve("test.yml");
    Files.writeString(f, YAML);

    var file = Loaders.file(f);
    assertThat(file.text()).isEqualTo(YAML);
    assertThat(file.id()).isEqualTo("file:" + f);
  }

  /* ───────────────── jar loader ────────────────────────────────── */
  @Test
  void jarLoader() throws Exception {
    Path jar = createJar();
    String entry = "nested/cfg.json";

    /* build jar with single entry */
    try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jar))) {
      jos.putNextEntry(new JarEntry(entry));
      jos.write(JSON.getBytes());
      jos.closeEntry();
    }

    var jl = Loaders.jar(jar, entry);
    assertThat(jl.text()).isEqualTo(JSON);
    assertThat(jl.id()).isEqualTo("jar:" + jar + "!" + entry);
  }

  /* ───────────────── url loader (localhost http) ───────────────── */
  @Test
  void urlLoader() throws Exception {
    /* запускаем минимальный HTTP-сервер */
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/test.yml",
        h -> {
          byte[] body = YAML.getBytes();
          h.sendResponseHeaders(200, body.length);
          try (OutputStream os = h.getResponseBody()) {
            os.write(body);
          }
        });
    ExecutorService es = Executors.newSingleThreadExecutor();
    server.setExecutor(es);
    server.start();

    URI uri = new URI("http://localhost:" + server.getAddress().getPort() + "/test.yml");
    var url = Loaders.url(uri);

    assertThat(url.text()).isEqualTo(YAML);
    assertThat(url.id()).isEqualTo(uri.toString());

    server.stop(0);
    es.shutdownNow();
  }

  /* ───────────────── bytes() helper returns full array ─────────── */
  @Test
  void bytesHelperRoundtrip() throws Exception {
    Path f = tmp.resolve("test.json");
    Files.writeString(f, JSON);
    var r = Loaders.file(f);
    byte[] raw = r.bytes();
    assertThat(new ObjectMapper().readTree(raw).get("hello").asText()).isEqualTo("world");
  }

  private Path createJar() throws Exception {
    Path jarFile = Files.createTempFile("demo", ".jar");
    String entry = "cfg/limits.yml";
    String content =
        """
                user:
                  permits: 5
                  seconds: 60
                """;

    // try-with-resources гарантирует закрытие
    try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile))) {
      // Обязательно: *нормализованный* путь без ведущего `/`
      jos.putNextEntry(new JarEntry(entry));
      jos.write(content.getBytes(UTF_8));
      jos.closeEntry();
    }

    // Теперь jarFile готов → передаём в JarResourceLoader
    byte[] raw = Loaders.jar(jarFile, entry).bytes();
    assertEquals(content, new String(raw, UTF_8));
    return jarFile;
  }
}
