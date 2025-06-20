package io.lonmstalker.tgkit.doc.scraper;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Реализация {@link DocScraper} на базе Jsoup.
 */
public class JsoupDocScraper implements DocScraper {
  @Override
  public List<MethodDoc> scrape(InputStream stream) {
    Document doc;
    try {
      doc = Jsoup.parse(stream, null, "");
    } catch (Exception e) {
      throw new IllegalArgumentException("Не удалось распарсить документ", e);
    }
    Elements methods = doc.select("div.method");
    return methods.stream().map(this::parseMethod).collect(Collectors.toList());
  }

  private MethodDoc parseMethod(Element el) {
    String name = el.selectFirst("h3").text();
    String desc = el.selectFirst("p").text();
    return new MethodDoc(name, desc);
  }
}
