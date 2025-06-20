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
package io.github.tgkit.doc.scraper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class BotApiScraperTest {
  @Test
  void downloadsAndParses() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/bots/api", new TestHandler());
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    Path cache = Files.createTempDirectory("cache");
    BotApiScraper scraper = new BotApiScraper(HttpClient.newHttpClient(), uri, cache);
    List<MethodDoc> methods = scraper.fetch();
    server.stop(0);
    assertThat(methods).hasSize(1);
    assertThat(methods.get(0).name()).isEqualTo("getMe");
  }

  @Test
  void usesCacheOnNotModified() throws IOException {
    AtomicInteger counter = new AtomicInteger();
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/bots/api",
        exchange -> {
          if (counter.getAndIncrement() == 0) {
            String html = "<div class='method'><h3>getMe</h3><p>desc</p></div>";
            exchange.getResponseHeaders().add("ETag", "v1");
            exchange.sendResponseHeaders(200, html.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
              os.write(html.getBytes());
            }
          } else {
            exchange.sendResponseHeaders(304, -1);
          }
        });
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    Path cache = Files.createTempDirectory("cache");
    BotApiScraper scraper = new BotApiScraper(HttpClient.newHttpClient(), uri, cache);
    scraper.fetch();
    List<MethodDoc> again = scraper.fetch();
    server.stop(0);
    assertThat(again).hasSize(1);
    assertThat(counter.get()).isEqualTo(2);
  }

  @Test
  void failsOnBadStatus() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/bots/api",
        exchange -> {
          exchange.sendResponseHeaders(500, -1);
        });
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    BotApiScraper scraper =
        new BotApiScraper(HttpClient.newHttpClient(), uri, Files.createTempDirectory("cache"));
    try {
      scraper.fetch();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageContaining("HTTP");
    } finally {
      server.stop(0);
    }
  }

  @Test
  void failsOnNotModifiedWithoutCache() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext(
        "/bots/api",
        exchange -> {
          exchange.sendResponseHeaders(304, -1);
        });
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    BotApiScraper scraper =
        new BotApiScraper(HttpClient.newHttpClient(), uri, Files.createTempDirectory("cache"));
    try {
      scraper.fetch();
      fail("expected exception");
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageContaining("HTTP");
    } finally {
      server.stop(0);
    }
  }

  @Test
  void handlesEmptyEtagFile() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/bots/api", new TestHandler());
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    Path cache = Files.createTempDirectory("cache");
    Files.createFile(cache.resolve("etag"));
    BotApiScraper scraper = new BotApiScraper(HttpClient.newHttpClient(), uri, cache);
    List<MethodDoc> methods = scraper.fetch();
    server.stop(0);
    assertThat(methods).hasSize(1);
  }

  @Test
  void readsBaseUriFromProperty() throws Exception {
    HttpServer server1 = HttpServer.create(new InetSocketAddress(0), 0);
    server1.createContext("/bots/api", new TestHandler());
    server1.start();
    HttpServer server2 = HttpServer.create(new InetSocketAddress(0), 0);
    server2.createContext(
        "/bots/api",
        exchange -> {
          String html = "<div class='method'><h3>sendMessage</h3><p>desc</p></div>";
          exchange.sendResponseHeaders(200, html.getBytes().length);
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(html.getBytes());
          }
        });
    server2.start();

    String originalHome = System.getProperty("user.home");
    Path tmpHome = Files.createTempDirectory("home");
    System.setProperty("user.home", tmpHome.toString());
    try {
      System.setProperty(
          "tg.doc.baseUri", "http://localhost:" + server1.getAddress().getPort() + "/bots/api");
      List<MethodDoc> first = new BotApiScraper().fetch();
      System.setProperty(
          "tg.doc.baseUri", "http://localhost:" + server2.getAddress().getPort() + "/bots/api");
      List<MethodDoc> second = new BotApiScraper().fetch();

      assertThat(first.get(0).name()).isEqualTo("getMe");
      assertThat(second.get(0).name()).isEqualTo("sendMessage");
    } finally {
      System.setProperty("user.home", originalHome);
      System.clearProperty("tg.doc.baseUri");
      server1.stop(0);
      server2.stop(0);
    }
  }

  private static class TestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
      String html = "<div class='method'><h3>getMe</h3><p>desc</p></div>";
      exchange.sendResponseHeaders(200, html.getBytes().length);
      try (OutputStream os = exchange.getResponseBody()) {
        os.write(html.getBytes());
      }
    }
  }
}
