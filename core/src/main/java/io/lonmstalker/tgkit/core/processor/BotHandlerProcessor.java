package io.lonmstalker.tgkit.core.processor;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
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

@SupportedSourceVersion(SourceVersion.RELEASE_21)
@SupportedAnnotationTypes("io.lonmstalker.tgkit.core.annotation.BotHandler")
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
