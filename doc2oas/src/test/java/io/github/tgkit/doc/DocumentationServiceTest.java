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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.tgkit.doc.emitter.OpenApiEmitter;
import io.github.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DocumentationServiceTest {
  @Test
  void generatesFromHtml() throws Exception {
    Path html = Files.createTempFile("doc", ".html");
    Files.writeString(html, "<div class='method'><h3>getMe</h3><p>desc</p></div>");
    Path out = Files.createTempFile("spec", ".yaml");
    new DocumentationService().generate(html, out);
    assertThat(out.toFile()).exists();
    assertThat(Files.readString(out)).contains("getMe");
  }

  @Test
  void warnsOnChanges() throws Exception {
    OpenApiEmitter emitter = new OpenApiEmitter();
    Path previous = Files.createTempFile("prev", ".yaml");
    emitter.write(emitter.toOpenApi(List.of(new OperationInfo("getMe", "desc"))), previous);
    Path current = Files.createTempFile("new", ".yaml");
    emitter.write(emitter.toOpenApi(List.of(new OperationInfo("getChat", "desc"))), current);

    var err = new java.io.ByteArrayOutputStream();
    var original = System.err;
    System.setErr(new java.io.PrintStream(err));
    try {
      new DocumentationService().validate(previous, current);
    } finally {
      System.setErr(original);
    }

    String warnings = err.toString();
    assertThat(warnings).contains("getChat");
    assertThat(warnings).contains("getMe");
  }

  @Test
  void warnsOnModelChanges() throws Exception {
    Path previous = Files.createTempFile("prev", ".yaml");
    OpenApiEmitter emitter = new OpenApiEmitter();
    emitter.write(buildApi("getMe", "User"), previous);

    Path current = Files.createTempFile("new", ".yaml");
    emitter.write(buildApi("getMe", "Chat"), current);

    var err = new java.io.ByteArrayOutputStream();
    var original = System.err;
    System.setErr(new java.io.PrintStream(err));
    try {
      new DocumentationService().validate(previous, current);
    } finally {
      System.setErr(original);
    }

    String warnings = err.toString();
    assertThat(warnings).contains("User");
    assertThat(warnings).contains("Chat");
    assertThat(warnings).contains("getMe");
  }

  @Test
  void failsOnInvalidSpec() throws Exception {
    Path prev = Files.createTempFile("prev", ".yaml");
    Path curr = Files.createTempFile("curr", ".yaml");
    Files.writeString(prev, "???");
    Files.writeString(curr, "???");

    DocumentationService service = new DocumentationService();
    assertThatThrownBy(() -> service.validate(prev, curr))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void collectsComposedSchemaRefs() throws Exception {
    var method =
        DocumentationService.class.getDeclaredMethod("collectSchemaRefs", Schema.class, Set.class);
    method.setAccessible(true);
    ComposedSchema composed = new ComposedSchema();
    composed.setAllOf(List.of(new Schema<>().$ref("#/components/schemas/User")));
    Schema<?> base = new Schema<>().properties(Map.of("c", composed));
    Set<String> models = new HashSet<>();

    method.invoke(null, base, models);

    assertThat(models).contains("User");
  }

  private static OpenAPI buildApi(String operationId, String schemaName) {
    Schema<?> schemaRef = new Schema<>().$ref("#/components/schemas/" + schemaName);
    ApiResponse ok =
        new ApiResponse()
            .description("OK")
            .content(
                new Content().addMediaType("application/json", new MediaType().schema(schemaRef)));
    Operation op =
        new Operation()
            .operationId(operationId)
            .responses(new ApiResponses().addApiResponse("200", ok));
    OpenAPI api =
        new OpenAPI()
            .info(new Info().title("t").version("1"))
            .components(new Components().addSchemas(schemaName, new ObjectSchema()))
            .paths(new Paths().addPathItem("/" + operationId, new PathItem().post(op)));
    return api;
  }
}
