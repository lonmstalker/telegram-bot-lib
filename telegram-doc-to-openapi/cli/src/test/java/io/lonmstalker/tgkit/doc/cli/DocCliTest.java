package io.lonmstalker.tgkit.doc.cli;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class DocCliTest {
  @Test
  void generatesFileFromHtml() throws Exception {
    Path html = Files.createTempFile("doc", ".html");
    Files.writeString(html, "<div class='method'><h3>getMe</h3><p>desc</p></div>");
    Path out = Files.createTempDirectory("out").resolve("telegram.yaml");
    int code = new CommandLine(new DocCli()).execute("--input", html.toString(), "--output", out.toString());
    assertThat(code).isZero();
    assertThat(Files.readString(out)).contains("getMe");
  }
}
