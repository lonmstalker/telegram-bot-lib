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
