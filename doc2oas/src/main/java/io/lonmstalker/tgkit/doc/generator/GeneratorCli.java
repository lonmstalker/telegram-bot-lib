package io.lonmstalker.tgkit.doc.generator;

import java.nio.file.Path;
import java.util.Map;
import info.picocli.CommandLine;
import info.picocli.CommandLine.Command;
import info.picocli.CommandLine.Option;
import org.openapitools.codegen.DefaultGenerator;
import org.openapitools.codegen.config.CodegenConfigurator;
import org.openapitools.codegen.config.ClientOptInput;

/**
 * CLI-обёртка OpenAPI Generator.
 */
@Command(name = "generate", mixinStandardHelpOptions = true)
public class GeneratorCli implements Runnable {

  @Option(names = "--spec", required = true, description = "Файл спецификации")
  private Path spec;

  @Option(names = "--target", required = true, description = "Каталог для кода")
  private Path target;

  @Option(names = "--language", defaultValue = "java", description = "Язык SDK")
  private String language;

  @Override
  public void run() {
    CodegenConfigurator cfg = new CodegenConfigurator()
        .setLang(language)
        .setInputSpec(spec.toString())
        .setOutputDir(target.toString())
        .setAdditionalProperties(Map.of(
            "withRecordModels", "true",
            "openApiNullable", "false"));
    ClientOptInput input = cfg.toClientOptInput();
    new DefaultGenerator().opts(input).generate();
  }

  /** Точка входа. */
  public static void main(String[] args) {
    new CommandLine(new GeneratorCli()).execute(args);
  }
}
