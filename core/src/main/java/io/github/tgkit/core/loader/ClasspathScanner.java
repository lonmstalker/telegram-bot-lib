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
package io.github.tgkit.core.loader;

import java.lang.annotation.Annotation;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

/** Utility for scanning the classpath for annotated classes. */
public final class ClasspathScanner {
  private ClasspathScanner() {}

  /** Finds all classes annotated with the given annotation under the package. */
  public static @NonNull Set<Class<?>> findAnnotated(
      @NonNull Class<? extends Annotation> annotation, @NonNull String basePackage) {
    ConfigurationBuilder cb =
        new ConfigurationBuilder().forPackages(basePackage).addScanners(Scanners.TypesAnnotated);
    Reflections reflections = new Reflections(cb);
    return reflections.getTypesAnnotatedWith(annotation);
  }
}
