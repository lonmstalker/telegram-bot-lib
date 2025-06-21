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

package io.github.tgkit.doc;

import io.github.tgkit.doc.emitter.OpenApiEmitter;
import io.github.tgkit.doc.mapper.MethodDocMapper;
import io.github.tgkit.doc.scraper.BotApiScraper;
import io.github.tgkit.doc.scraper.DocScraper;
import io.github.tgkit.doc.scraper.JsoupDocScraper;
import io.github.tgkit.doc.scraper.MethodDoc;
import io.swagger.v3.oas.models.OpenAPI;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Сервис преобразования документации Telegram API в спецификацию OpenAPI.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * DocumentationService service = new DocumentationService();
 * service.generate(Path.of("index.html"), Path.of("build/telegram.yaml"));
 * }</pre>
 */
public final class DocumentationService {

  private final DocScraper htmlScraper = new JsoupDocScraper();
  private final BotApiScraper apiScraper = new BotApiScraper();
  private final MethodDocMapper mapper = MethodDocMapper.INSTANCE;
  private final OpenApiEmitter emitter = new OpenApiEmitter();

  /**
   * Генерирует файл OpenAPI из HTML-документа.
   *
   * @param input  путь к исходному HTML
   * @param output путь для сохранения YAML
   */
  public void generate(@NonNull Path input, @NonNull Path output) {
    try (InputStream in = Files.newInputStream(input)) {
      List<MethodDoc> docs = htmlScraper.scrape(in);
      write(docs, output);
    } catch (IOException e) {
      throw new UncheckedIOException("Не удалось обработать документ", e);
    }
  }

  /**
   * Скачивает официальную документацию и сохраняет YAML.
   */
  public void generateFromApi(@NonNull Path output) {
    List<MethodDoc> docs = apiScraper.fetch();
    write(docs, output);
  }

  private void write(@NonNull List<MethodDoc> docs, @NonNull Path output) {
    OpenAPI api = emitter.toOpenApi(docs.stream().map(mapper::toOperation).toList());
    emitter.write(api, output);
  }
}
