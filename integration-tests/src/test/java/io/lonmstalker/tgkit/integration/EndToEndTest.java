package io.lonmstalker.tgkit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.lonmstalker.tgkit.doc.cli.DocCli;
import io.lonmstalker.tgkit.generator.GeneratorCli;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

/** Интеграционный тест полного конвейера. */
class EndToEndTest {

  private WireMockServer server;

  @BeforeEach
  void startServer() {
    server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    server.start();
    server.stubFor(WireMock.get("/bots/api").willReturn(WireMock.aResponse()
        .withHeader("Content-Type", "text/html")
        .withBody("<div class='method'><h3>getMe</h3><p>desc</p></div>")));
  }

  @AfterEach
  void stopServer() {
    server.stop();
  }

  @Test
  void fullPipeline() throws Exception {
    Path dir = Files.createTempDirectory("it");
    Path spec = dir.resolve("telegram.yaml");
    Path target = dir.resolve("sdk");
    System.setProperty("tg.doc.baseUri", server.baseUrl() + "/bots/api");

    int code1 = new CommandLine(new DocCli()).execute("--api", "--output", spec.toString());
    assertThat(code1).isZero();
    int code2 = new CommandLine(new GeneratorCli()).execute("--spec", spec.toString(),
        "--target", target.toString());
    assertThat(code2).isZero();
    assertThat(Files.exists(target)).isTrue();
  }
}
