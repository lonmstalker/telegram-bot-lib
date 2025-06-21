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
package io.github.tgkit.doc.generator;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.doc.emitter.OpenApiEmitter;
import io.github.tgkit.doc.mapper.OperationInfo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

class GeneratorCliTest {
  @Test
  void generatesSdk() throws Exception {
    OpenApiEmitter emitter = new OpenApiEmitter();
    Path spec = Files.createTempFile("spec", ".yaml");
    Path target = Files.createTempDirectory("sdk");
    emitter.write(emitter.toOpenApi(List.of(new OperationInfo("getMe", "desc"))), spec);
    int code =
        new CommandLine(new GeneratorCli())
            .execute("--spec", spec.toString(), "--target", target.toString());
    assertThat(code).isZero();
    assertThat(Files.exists(target.resolve("pom.xml"))).isTrue();
  }

  @Test
  void mainMethodRuns() throws Exception {
    OpenApiEmitter emitter = new OpenApiEmitter();
    Path spec = Files.createTempFile("spec", ".yaml");
    Path target = Files.createTempDirectory("sdk");
    emitter.write(emitter.toOpenApi(List.of(new OperationInfo("getMe", "desc"))), spec);
    GeneratorCli.main(new String[] {"--spec", spec.toString(), "--target", target.toString()});
    assertThat(Files.exists(target.resolve("pom.xml"))).isTrue();
  }
}
