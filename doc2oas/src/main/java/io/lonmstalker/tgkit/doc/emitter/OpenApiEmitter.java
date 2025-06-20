package io.lonmstalker.tgkit.doc.emitter;

import io.lonmstalker.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Построитель и писатель спецификации OpenAPI.
 */
public class OpenApiEmitter {
  /**
   * Формирует объект {@link OpenAPI} по списку операций.
   */
  public OpenAPI toOpenApi(List<OperationInfo> operations) {
    OpenAPI openApi = new OpenAPI();
    Paths paths = new Paths();
    for (OperationInfo op : operations) {
      Operation operation = new Operation();
      operation.setSummary(op.description());
      operation.setOperationId(op.name());
      PathItem path = new PathItem().post(operation);
      paths.addPathItem("/" + op.name(), path);
    }
    openApi.setPaths(paths);
    return openApi;
  }

  /**
   * Сохраняет YAML-файл со схемой OpenAPI.
   */
  public void write(OpenAPI openApi, Path file) {
    try {
      Files.createDirectories(file.getParent());
      String yaml = Yaml.mapper().writeValueAsString(openApi);
      SwaggerParseResult result = new OpenAPIV3Parser().readContents(yaml);
      if (!result.getMessages().isEmpty()) {
        throw new IllegalStateException("OpenAPI errors: " + result.getMessages());
      }
      Files.writeString(file, yaml);
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось записать OpenAPI", e);
    }
  }
}
