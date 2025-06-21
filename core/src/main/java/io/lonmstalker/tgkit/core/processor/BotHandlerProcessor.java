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

package io.github.tgkit.core.processor;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.annotation.Arg;
import io.github.tgkit.core.annotation.BotHandler;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Аннотационный процессор, проверяющий методы {@link BotHandler} во время компиляции.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * class MyBot {
 *   @BotHandler
 *   BotResponse onUpdate(BotRequest<?> req) {
 *     return new BotResponse();
 *   }
 * }
 * }</pre>
 */
@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes("io.github.tgkit.core.annotation.BotHandler")
public class BotHandlerProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    TypeMirror botResponse =
        processingEnv.getElementUtils().getTypeElement(BotResponse.class.getName()).asType();
    TypeMirror botRequest =
        processingEnv.getElementUtils().getTypeElement(BotRequest.class.getName()).asType();

    for (Element e : roundEnv.getElementsAnnotatedWith(BotHandler.class)) {
      if (e.getKind() != ElementKind.METHOD) {
        error("@BotHandler может быть применён только к методам", e);
        continue;
      }
      ExecutableElement method = (ExecutableElement) e;
      if (method.getModifiers().contains(Modifier.STATIC)) {
        error("Метод не должен быть static", method);
      }
      if (!processingEnv.getTypeUtils().isSameType(method.getReturnType(), botResponse)) {
        error("Метод должен возвращать BotResponse", method);
      }
      int requestParams = 0;
      for (VariableElement p : method.getParameters()) {
        Arg arg = p.getAnnotation(Arg.class);
        if (arg == null) {
          requestParams++;
          // стираем дженерики у параметра и сравниваем с BotRequest
          TypeMirror paramErased = processingEnv.getTypeUtils().erasure(p.asType());
          TypeMirror botRequestErased = processingEnv.getTypeUtils().erasure(botRequest);
          if (!processingEnv.getTypeUtils().isSameType(paramErased, botRequestErased)) {
            error(
                String.format(
                    "Неаннотированные параметры должны иметь тип %s, вы передали %s",
                    botRequest, p.asType()),
                p);
          }
        } else if (arg.value().isEmpty()) {
          error("Параметр @Arg должен содержать имя группы", p);
        }
      }
      if (requestParams == 0) {
        error("Метод должен принимать параметр BotRequest", method);
      } else if (requestParams > 1) {
        error("Допустим только один параметр BotRequest", method);
      }
    }
    return false;
  }

  private void error(String msg, Element e) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e);
  }
}
