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

package io.github.tgkit.doc.emitter;

import io.github.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.core.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Построитель и писатель спецификации OpenAPI.
 */
public class OpenApiEmitter {

  /**
   * Формирует объект {@link OpenAPI} по списку операций.
   */
  public @NonNull OpenAPI toOpenApi(@NonNull List<OperationInfo> operations) {
    OpenAPI openApi = new OpenAPI();
    openApi.setInfo(new Info().title("Telegram Bot API").version("1.0"));

    Paths paths = new Paths();
    ApiResponse ok = new ApiResponse().description("OK");
    for (OperationInfo op : operations) {
      Operation operation =
          new Operation()
              .summary(op.description())
              .operationId(op.name())
              .responses(new ApiResponses().addApiResponse("200", ok));

      PathItem path = new PathItem().post(operation);
      paths.addPathItem("/" + op.name(), path);
    }
    openApi.setPaths(paths);
    return openApi;
  }

  /**
   * Сохраняет YAML-файл со схемой OpenAPI.
   */
  public void write(@NonNull OpenAPI openApi, @NonNull Path file) {
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
