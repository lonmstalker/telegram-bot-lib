package io.lonmstalker.tgkit.doc.emitter;

import io.lonmstalker.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Конструктор спецификации OpenAPI и запись на диск.
 */
public class OpenApiEmitter {
  /** Формирует объект OpenAPI по операциям. */
  public OpenAPI toOpenApi(List<OperationInfo> operations) {
    OpenAPI api = new OpenAPI();
    Paths paths = new Paths();
    for (OperationInfo op : operations) {
      Operation operation = new Operation();
      operation.setSummary(op.description());
      PathItem item = new PathItem().post(operation);
      paths.addPathItem("/" + op.name(), item);
    }
    api.setPaths(paths);
    return api;
  }

  /** Пишет YAML-файл. */
  public void write(OpenAPI api, Path file) {
    try {
      Files.createDirectories(file.getParent());
      Files.writeString(file, Yaml.mapper().writeValueAsString(api));
    } catch (IOException e) {
      throw new IllegalStateException("Не удалось записать YAML", e);
    }
  }
}
