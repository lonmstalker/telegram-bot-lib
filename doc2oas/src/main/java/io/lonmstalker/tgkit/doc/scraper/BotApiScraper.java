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
package io.lonmstalker.tgkit.doc.scraper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/** Скачивает официальную документацию Telegram Bot API и парсит её как HTML. */
public class BotApiScraper extends JsoupDocScraper {
  private static final URI DEFAULT_URI =
      URI.create(System.getProperty("tg.doc.baseUri", "https://core.telegram.org/bots/api"));

  private final HttpClient client;
  private final URI uri;
  private final Path cacheDir;

  public BotApiScraper() {
    this(HttpClient.newHttpClient(), DEFAULT_URI, defaultCache());
  }

  BotApiScraper(HttpClient client, URI uri) {
    this(client, uri, defaultCache());
  }

  BotApiScraper(HttpClient client, URI uri, Path cacheDir) {
    this.client = client;
    this.uri = uri;
    this.cacheDir = cacheDir;
  }

  /** Загружает страницу API и возвращает список методов. */
  public List<MethodDoc> fetch() {
    try {
      Files.createDirectories(cacheDir);
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось создать каталог кеша", e);
    }
    Path htmlFile = cacheDir.resolve("telegram.html");
    Path etagFile = cacheDir.resolve("etag");

    HttpRequest.Builder builder = HttpRequest.newBuilder(uri).GET();
    if (Files.exists(etagFile)) {
      try {
        String etag = Files.readString(etagFile).trim();
        if (!etag.isEmpty()) {
          builder.header("If-None-Match", etag);
        }
      } catch (IOException ignored) {
        // игнорируем повреждённый кеш
      }
    }

    try {
      HttpResponse<InputStream> resp =
          client.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());
      if (resp.statusCode() == 304 && Files.exists(htmlFile)) {
        try (InputStream stream = Files.newInputStream(htmlFile)) {
          return scrape(stream);
        }
      }

      if (resp.statusCode() != 200) {
        throw new IllegalStateException("HTTP " + resp.statusCode());
      }

      try (InputStream body = resp.body()) {
        Files.copy(body, htmlFile, StandardCopyOption.REPLACE_EXISTING);
      }
      resp.headers()
          .firstValue("ETag")
          .ifPresent(
              etag -> {
                try {
                  Files.writeString(etagFile, etag);
                } catch (IOException ignored) {
                }
              });

      try (InputStream stream = Files.newInputStream(htmlFile)) {
        return scrape(stream);
      }
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Не удалось загрузить API", e);
    }
  }

  private static Path defaultCache() {
    return Path.of(System.getProperty("user.home"), ".cache", "tgdoc");
  }
}
