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
package io.github.tgkit.doc.cli;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class DocCliTest {
  @Test
  void generatesFile() throws Exception {
    Path html = Files.createTempFile("doc", ".html");
    Files.writeString(html, "<div class='method'><h3>getMe</h3><p>desc</p></div>");
    Path out = Files.createTempDirectory("out").resolve("telegram.yaml");
    int code =
        new CommandLine(new DocCli())
            .execute("--input", html.toString(), "--output", out.toString());
    assertThat(code).isZero();
    assertThat(out.toFile()).exists();
    assertThat(Files.readString(out)).contains("getMe");
  }

  @Test
  void failsWithoutInputAndFlag() {
    int code = new CommandLine(new DocCli()).execute();
    assertThat(code).isEqualTo(CommandLine.ExitCode.USAGE);
  }

  @Test
  void mainMethodExecutes() throws Exception {
    Path html = Files.createTempFile("doc", ".html");
    Files.writeString(html, "<div class='method'><h3>getMe</h3><p>desc</p></div>");
    Path out = Files.createTempDirectory("out").resolve("telegram.yaml");
    DocCli.main(new String[] {"--input", html.toString(), "--output", out.toString()});
    assertThat(out.toFile()).exists();
  }

  @Test
  void generatesFromApi() throws Exception {
    WireMockServer server = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    server.start();
    server.stubFor(
        WireMock.get("/bots/api")
            .willReturn(
                WireMock.aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withBody("<div class='method'><h3>getMe</h3><p>desc</p></div>")));
    String originalHome = System.getProperty("user.home");
    Path tmpHome = Files.createTempDirectory("home");
    System.setProperty("user.home", tmpHome.toString());
    System.setProperty("tg.doc.baseUri", server.baseUrl() + "/bots/api");
    try {
      Path out = Files.createTempDirectory("out").resolve("telegram.yaml");
      int code = new CommandLine(new DocCli()).execute("--api", "--output", out.toString());
      assertThat(code).isZero();
      assertThat(Files.readString(out)).contains("getMe");
    } finally {
      System.setProperty("user.home", originalHome);
      System.clearProperty("tg.doc.baseUri");
      server.stop();
    }
  }
}
