package io.lonmstalker.tgkit.doc.scraper;

import java.io.InputStream;
import java.util.List;

/**
 * Интерфейс парсера HTML-документации Telegram API.
 *
 * <p>Пример:
 * <pre>{@code
 * DocScraper scraper = new JsoupDocScraper();
 * List<MethodDoc> docs = scraper.scrape(stream);
 * }</pre>
 */
public interface DocScraper {
  /** Читает HTML и возвращает список методов. */
  List<MethodDoc> scrape(InputStream stream);
}
