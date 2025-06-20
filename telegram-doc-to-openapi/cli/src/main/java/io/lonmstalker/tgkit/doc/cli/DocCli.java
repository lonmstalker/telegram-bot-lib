package io.lonmstalker.tgkit.doc.cli;

import io.lonmstalker.tgkit.doc.DocumentationService;
import java.nio.file.Path;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Консольная утилита для генерации OpenAPI.
 */
@Command(name = "doc-to-openapi", mixinStandardHelpOptions = true)
public class DocCli implements Runnable {
  @Option(names = "--input", description = "HTML файл с документацией")
  private Path input;

  @Option(names = "--api", description = "Загрузить официальную документацию")
  private boolean fromApi;

  @Option(names = "--output", description = "Файл YAML", defaultValue = "${sys:user.dir}/build/openapi/telegram.yaml")
  private Path output;

  @Override
  public void run() {
    DocumentationService service = new DocumentationService();
    if (fromApi) {
      service.generateFromApi(output);
    } else {
      service.generate(input, output);
    }
  }

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new DocCli()).execute(args);
  }
}
