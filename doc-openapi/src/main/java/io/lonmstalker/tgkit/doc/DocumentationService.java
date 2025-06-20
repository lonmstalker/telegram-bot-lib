package io.lonmstalker.tgkit.doc;

import io.lonmstalker.tgkit.doc.emitter.OpenApiEmitter;
import io.lonmstalker.tgkit.doc.mapper.MethodDocMapper;
import io.lonmstalker.tgkit.doc.scraper.DocScraper;
import io.lonmstalker.tgkit.doc.scraper.JsoupDocScraper;
import io.lonmstalker.tgkit.doc.scraper.MethodDoc;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Сервис преобразования документации Telegram API в спецификацию OpenAPI.
 *
 * <p>Пример использования:
 * <pre>{@code
 * DocumentationService service = new DocumentationService();
 * service.generate(Path.of("index.html"), Path.of("build/telegram.yaml"));
 * }</pre>
 */
public final class DocumentationService {

  private final DocScraper scraper = new JsoupDocScraper();
  private final MethodDocMapper mapper = MethodDocMapper.INSTANCE;
  private final OpenApiEmitter emitter = new OpenApiEmitter();

  /**
   * Генерирует файл OpenAPI из HTML-документа.
   *
   * @param input  путь к исходному HTML
   * @param output путь для сохранения YAML
   */
  public void generate(Path input, Path output) {
    try (InputStream in = Files.newInputStream(input)) {
      List<MethodDoc> docs = scraper.scrape(in);
      OpenAPI api = emitter.toOpenApi(docs.stream().map(mapper::toOperation).toList());
      emitter.write(api, output);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось обработать документ", e);
    }
  }
}
