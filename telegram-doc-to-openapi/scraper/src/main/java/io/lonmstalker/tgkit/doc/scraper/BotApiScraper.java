package io.lonmstalker.tgkit.doc.scraper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Скачивает официальную документацию Telegram Bot API и парсит её как HTML.
 */
public class BotApiScraper extends HtmlDocScraper {
  private static final URI DEFAULT_URI = URI.create("https://core.telegram.org/bots/api");

  private final HttpClient client;
  private final URI uri;

  public BotApiScraper() {
    this(HttpClient.newHttpClient(), DEFAULT_URI);
  }

  BotApiScraper(HttpClient client, URI uri) {
    this.client = client;
    this.uri = uri;
  }

  /** Загружает страницу API и возвращает список методов. */
  public List<MethodDoc> fetch() {
    HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
    try {
      HttpResponse<InputStream> resp = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
      try (InputStream stream = resp.body()) {
        return scrape(stream);
      }
    } catch (IOException | InterruptedException e) {
      throw new IllegalStateException("Не удалось загрузить API", e);
    }
  }
}
