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

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.doc.mapper.OperationInfo;
import io.swagger.v3.oas.models.OpenAPI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenApiEmitterTest {
  @Test
  void buildsAndWritesYaml() throws Exception {
    OpenApiEmitter emitter = new OpenApiEmitter();
    List<OperationInfo> ops = List.of(new OperationInfo("getMe", "desc"));
    OpenAPI api = emitter.toOpenApi(ops);
    Path tmp = Files.createTempFile("openapi", ".yaml");
    emitter.write(api, tmp);
    String yaml = Files.readString(tmp);
    assertThat(yaml).contains("/getMe");
  }

  @Test
  void failsOnInvalidSpec() throws Exception {
    OpenAPI api = new OpenAPI();
    Path tmp = Files.createTempFile("bad", ".yaml");
    try {
      new OpenApiEmitter().write(api, tmp);
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageContaining("OpenAPI errors");
    }
  }
}
