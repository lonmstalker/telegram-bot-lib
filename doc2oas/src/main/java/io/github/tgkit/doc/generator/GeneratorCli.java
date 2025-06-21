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

import java.nio.file.Path;
import java.util.Map;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import picocli.CommandLine;

/** CLI-обёртка OpenAPI Generator. */
@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true)
public class GeneratorCli implements Runnable {

  @CommandLine.Option(names = "--spec", required = true, description = "Файл спецификации")
  private Path spec;

  @CommandLine.Option(names = "--target", required = true, description = "Каталог для кода")
  private Path target;

  @CommandLine.Option(names = "--language", defaultValue = "java", description = "Язык SDK")
  private String language;

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new GeneratorCli()).execute(args);
  }

  @Override
  public void run() {
    CodegenConfigurator cfg =
        new CodegenConfigurator()
            .setInputSpec(spec.toString())
            .setOutputDir(target.toString())
            .setGeneratorName(language)
            .setAdditionalProperties(
                Map.of(
                    "withRecordModels", "true",
                    "openApiNullable", "false"));
    ClientOptInput input = cfg.toClientOptInput();
    new DefaultGenerator().opts(input).generate();
  }
}
