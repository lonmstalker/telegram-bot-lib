package io.lonmstalker.tgkit.doc.scraper;

import java.io.InputStream;
import java.util.List;

/**
 * Интерфейс парсера документации Telegram API.
 */
public interface DocScraper {
  /**
   * Читает содержимое и возвращает список методов.
   */
  List<MethodDoc> scrape(InputStream stream);
}
