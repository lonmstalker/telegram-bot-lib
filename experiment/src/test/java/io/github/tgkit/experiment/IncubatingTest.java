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
package io.github.tgkit.experiment;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.Test;

/** Тесты для аннотации {@link Incubating}. */
public class IncubatingTest {

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

  @Incubating
  static class ExperimentalClass {
    @Incubating
    void method() {}
  }
}
