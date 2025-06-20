package io.lonmstalker.tgkit.doc;

import io.lonmstalker.tgkit.doc.emitter.OpenApiEmitter;
import io.lonmstalker.tgkit.doc.mapper.MethodDocMapper;
import io.lonmstalker.tgkit.doc.scraper.BotApiScraper;
import io.lonmstalker.tgkit.doc.scraper.DocScraper;
import io.lonmstalker.tgkit.doc.scraper.HtmlDocScraper;
import io.lonmstalker.tgkit.doc.scraper.MethodDoc;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Конвейер преобразования документации в OpenAPI.
 */
public final class DocumentationService {
  private final DocScraper htmlScraper = new HtmlDocScraper();
  private final BotApiScraper apiScraper = new BotApiScraper();
  private final MethodDocMapper mapper = MethodDocMapper.INSTANCE;
  private final OpenApiEmitter emitter = new OpenApiEmitter();

  /** Из HTML-файла. */
  public void generate(Path input, Path output) {
    try (InputStream in = Files.newInputStream(input)) {
      List<MethodDoc> docs = htmlScraper.scrape(in);
      write(docs, output);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось обработать файл", e);
    }
  }

  /** Скачивает официальную документацию и сохраняет YAML. */
  public void generateFromApi(Path output) {
    List<MethodDoc> docs = apiScraper.fetch();
    write(docs, output);
  }

  private void write(List<MethodDoc> docs, Path output) {
    OpenAPI api = emitter.toOpenApi(docs.stream().map(mapper::toOperation).toList());
    emitter.write(api, output);
  }
}
