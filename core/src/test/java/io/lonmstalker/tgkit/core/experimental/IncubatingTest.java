package io.lonmstalker.tgkit.core.experimental;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

/** Тесты для аннотации {@link Incubating}. */
public class IncubatingTest {

  @Incubating
  static class ExperimentalClass {
    @Incubating
    void method() {}
  }

  /** Проверяет настройки мета-аннотаций. */
  @Test
  void verifyMetaAnnotations() {
    Retention retention = Incubating.class.getAnnotation(Retention.class);
    assertNotNull(retention);
    assertEquals(RetentionPolicy.CLASS, retention.value());

    Target target = Incubating.class.getAnnotation(Target.class);
    assertNotNull(target);
    assertArrayEquals(new ElementType[] {ElementType.TYPE, ElementType.METHOD}, target.value());
  }

  /** Убеждаемся, что аннотация не доступна во время выполнения. */
  @Test
  void annotationNotVisibleAtRuntime() throws NoSuchMethodException {
    assertNull(ExperimentalClass.class.getAnnotation(Incubating.class));
    assertNull(ExperimentalClass.class.getDeclaredMethod("method").getAnnotation(Incubating.class));
  }
}
