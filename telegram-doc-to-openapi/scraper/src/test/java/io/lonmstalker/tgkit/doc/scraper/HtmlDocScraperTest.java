package io.lonmstalker.tgkit.doc.scraper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.util.List;
import org.junit.jupiter.api.Test;

class HtmlDocScraperTest {
  @Test
  void parsesMethodsFromHtml() {
    DocScraper scraper = new HtmlDocScraper();
    InputStream in = getClass().getResourceAsStream("/sample.html");
    List<MethodDoc> methods = scraper.scrape(in);
    assertThat(methods).hasSize(2);
    assertThat(methods.get(0).name()).isEqualTo("getMe");
  }
}
