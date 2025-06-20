package io.lonmstalker.tgkit.doc.scraper;

import static org.assertj.core.api.Assertions.assertThat;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class BotApiScraperTest {
  @Test
  void downloadsAndParses() throws IOException {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/bots/api", new TestHandler());
    server.start();
    URI uri = URI.create("http://localhost:" + server.getAddress().getPort() + "/bots/api");
    BotApiScraper scraper = new BotApiScraper(HttpClient.newHttpClient(), uri);
    List<MethodDoc> methods = scraper.fetch();
    server.stop(0);
    assertThat(methods).hasSize(1);
    assertThat(methods.get(0).name()).isEqualTo("getMe");
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
