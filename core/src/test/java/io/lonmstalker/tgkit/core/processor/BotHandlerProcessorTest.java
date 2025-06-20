package io.lonmstalker.tgkit.core.processor;

import static com.google.testing.compile.CompilationSubject.assertThat;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import java.util.Locale;
import javax.tools.JavaFileObject;
import org.junit.jupiter.api.Test;

class BotHandlerProcessorTest {

  @Test
  void messageShowsExpectedType() {
    JavaFileObject source =
        JavaFileObjects.forSourceLines(
            "test.Bad",
            "package test;",
            "import io.lonmstalker.tgkit.core.BotResponse;",
            "import io.lonmstalker.tgkit.core.annotation.BotHandler;",
            "public class Bad {",
            "  @BotHandler",
            "  public BotResponse bad(String value) { return null; }",
            "}");

    Compilation compilation =
        Compiler.javac().withProcessors(new BotHandlerProcessor()).compile(source);

    assertThat(compilation).failed();
    assertThat(compilation.errors())
        .anyMatch(
            d ->
                d.getMessage(Locale.ROOT).contains("BotRequest")
                    && d.getMessage(Locale.ROOT).contains("String"));
  }
}
