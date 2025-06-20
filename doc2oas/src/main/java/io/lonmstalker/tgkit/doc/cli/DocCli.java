package io.lonmstalker.tgkit.doc.cli;

import io.lonmstalker.tgkit.doc.DocumentationService;
import java.nio.file.Path;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Консольная утилита для генерации OpenAPI из HTML-документации.
 */
@Command(name = "doc-to-openapi", mixinStandardHelpOptions = true, version = "1.0")
public class DocCli implements Runnable {

  @Option(names = "--input", description = "Входной HTML файл")
  private Path input;

  @Option(names = "--api", description = "Загрузить документацию с сайта")
  private boolean fromApi;

  @Option(names = "--output", description = "Файл назначения YAML",
      defaultValue = "${sys:user.dir}/build/openapi/telegram.yaml")
  private Path output;

  @Override
  public void run() {
    DocumentationService service = new DocumentationService();
    if (fromApi) {
      service.generateFromApi(output);
    } else if (input != null) {
      service.generate(input, output);
    } else {
      throw new CommandLine.ParameterException(new CommandLine(this),
          "Не указан входной файл и не задан флаг --api");
    }
  }

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new DocCli()).execute(args);
  }
}
