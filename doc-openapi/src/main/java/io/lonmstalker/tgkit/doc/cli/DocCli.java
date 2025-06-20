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

  @Option(names = "--input", description = "Входной HTML файл", required = true)
  private Path input;

  @Option(names = "--output", description = "Файл назначения YAML",
      defaultValue = "${sys:user.dir}/build/openapi/telegram.yaml")
  private Path output;

  @Override
  public void run() {
    new DocumentationService().generate(input, output);
  }

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new DocCli()).execute(args);
  }
}
