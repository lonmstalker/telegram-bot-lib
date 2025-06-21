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
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
   * @param input путь к исходному HTML
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

  /** Скачивает официальную документацию и сохраняет YAML. */
  public void generateFromApi(@NonNull Path output) {
    List<MethodDoc> docs = apiScraper.fetch();
    write(docs, output);
  }

  private void write(@NonNull List<MethodDoc> docs, @NonNull Path output) {
    OpenAPI api = emitter.toOpenApi(docs.stream().map(mapper::toOperation).toList());
    emitter.write(api, output);
  }

  /**
   * Сравнивает предыдущую версию спецификации с текущей и печатает предупреждения.
   *
   * <p>Используется для отслеживания изменений в Telegram Bot API.
   *
   * @param previousSpec файл со старой схемой
   * @param newSpec файл со свежесгенерированной схемой
   */
  public void validate(@NonNull Path previousSpec, @NonNull Path newSpec) {
    OpenAPI oldApi = new OpenAPIV3Parser().read(previousSpec.toString());
    OpenAPI freshApi = new OpenAPIV3Parser().read(newSpec.toString());
    if (oldApi == null || freshApi == null) {
      throw new IllegalStateException("Не удалось прочитать спецификацию");
    }

    Set<String> oldOps = collectOperationIds(oldApi);
    Set<String> newOps = collectOperationIds(freshApi);
    Map<String, Set<String>> oldModels = collectModelsByOperation(oldApi);
    Map<String, Set<String>> newModels = collectModelsByOperation(freshApi);

    for (String op : newOps) {
      if (!oldOps.contains(op)) {
        System.err.println("WARN: добавлен метод " + op);
      }
    }
    for (String op : oldOps) {
      if (!newOps.contains(op)) {
        System.err.println("WARN: удален метод " + op);
      }
    }

    for (String op : newOps) {
      if (oldOps.contains(op)) {
        Set<String> oldSet = oldModels.getOrDefault(op, Set.of());
        Set<String> newSet = newModels.getOrDefault(op, Set.of());
        for (String model : newSet) {
          if (!oldSet.contains(model)) {
            System.err.println("WARN: операция " + op + " использует новую модель " + model);
          }
        }
        for (String model : oldSet) {
          if (!newSet.contains(model)) {
            System.err.println("WARN: операция " + op + " больше не использует модель " + model);
          }
        }
      }
    }
  }

  private static Set<String> collectOperationIds(OpenAPI api) {
    if (api.getPaths() == null) {
      return Set.of();
    }
    return api.getPaths().values().stream()
        .flatMap(p -> p.readOperations().stream())
        .map(op -> op.getOperationId() != null ? op.getOperationId() : "")
        .collect(java.util.stream.Collectors.toSet());
  }

  private static Map<String, Set<String>> collectModelsByOperation(OpenAPI api) {
    if (api.getPaths() == null) {
      return Map.of();
    }
    Map<String, Set<String>> result = new HashMap<>();
    api.getPaths()
        .values()
        .forEach(
            p ->
                p.readOperations()
                    .forEach(
                        op -> {
                          String id = op.getOperationId();
                          if (id == null) {
                            return;
                          }
                          Set<String> models = new HashSet<>();
                          if (op.getRequestBody() != null
                              && op.getRequestBody().getContent() != null) {
                            op.getRequestBody().getContent().values().stream()
                                .map(MediaType::getSchema)
                                .forEach(s -> collectSchemaRefs(s, models));
                          }
                          if (op.getResponses() != null) {
                            op.getResponses().values().stream()
                                .map(ApiResponse::getContent)
                                .filter(java.util.Objects::nonNull)
                                .flatMap(c -> c.values().stream())
                                .map(MediaType::getSchema)
                                .forEach(s -> collectSchemaRefs(s, models));
                          }
                          result.put(id, models);
                        }));
    return result;
  }

  private static void collectSchemaRefs(Schema<?> schema, Set<String> models) {
    if (schema == null) {
      return;
    }
    if (schema.get$ref() != null) {
      String ref = schema.get$ref();
      int idx = ref.lastIndexOf('/');
      models.add(idx >= 0 ? ref.substring(idx + 1) : ref);
    }
    if (schema instanceof ArraySchema array) {
      collectSchemaRefs(array.getItems(), models);
    }
    if (schema instanceof ComposedSchema composed) {
      if (composed.getAllOf() != null) {
        composed.getAllOf().forEach(s -> collectSchemaRefs(s, models));
      }
      if (composed.getAnyOf() != null) {
        composed.getAnyOf().forEach(s -> collectSchemaRefs(s, models));
      }
      if (composed.getOneOf() != null) {
        composed.getOneOf().forEach(s -> collectSchemaRefs(s, models));
      }
    }
    if (schema.getProperties() != null) {
      schema.getProperties().values().forEach(p -> collectSchemaRefs((Schema<?>) p, models));
    }
    Object additional = schema.getAdditionalProperties();
    if (additional instanceof Schema<?> addSchema) {
      collectSchemaRefs(addSchema, models);
    }
  }
}
