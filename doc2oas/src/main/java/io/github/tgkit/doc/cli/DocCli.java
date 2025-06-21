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
package io.github.tgkit.doc.cli;

import io.github.tgkit.doc.DocumentationService;
import java.nio.file.Path;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Консольная утилита для генерации OpenAPI из HTML-документации. */
@Command(name = "doc-to-openapi", mixinStandardHelpOptions = true, version = "1.0")
public class DocCli implements Runnable {

  @Option(names = "--input", description = "Входной HTML файл")
  private Path input;

  @Option(names = "--api", description = "Загрузить документацию с сайта")
  private boolean fromApi;

  @Option(
      names = "--output",
      description = "Файл назначения YAML",
      defaultValue = "${sys:user.dir}/build/openapi/telegram.yaml")
  private Path output;

  @Option(names = "--validate", description = "Путь к предыдущей спецификации")
  private Path previousSpec;

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new DocCli()).execute(args);
  }

  @Override
  /**
   * Запускает конвертацию документации в спецификацию OpenAPI.
   *
   * <p>Если указан {@code --api}, документ загружается с официального сайта. Иначе используется
   * локальный файл из опции {@code --input}. Результат сохраняется в путь из опции {@code
   * --output}.
   *
   * @throws CommandLine.ParameterException если не задан входной файл и не передан флаг {@code
   *     --api}
   */
  public void run() {
    DocumentationService service = new DocumentationService();
    if (fromApi) {
      service.generateFromApi(output);
    } else if (input != null) {
      service.generate(input, output);
    } else {
      throw new CommandLine.ParameterException(
          new CommandLine(this), "Не указан входной файл и не задан флаг --api");
    }
    if (previousSpec != null) {
      service.validate(previousSpec, output);
    }
  }
}
