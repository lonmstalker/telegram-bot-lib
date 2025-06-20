/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.plugin;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit-тесты для ChildFirstURLClassLoader. */
public class ChildFirstURLClassLoaderTest {

  private ChildFirstURLClassLoader classLoader;

  @BeforeEach
  void setUp() {
    // Передаём пустой URL-массив, парент — системный
    URL[] urls = new URL[0];
    classLoader = new ChildFirstURLClassLoader(urls, ClassLoader.getSystemClassLoader());
  }

  /** При первом запросе класса из кеша происходит поиск через родительский и кеширование. */
  @Test
  void testLoadClassCachesParentClass() throws ClassNotFoundException {
    // Загрузим стандартный класс String
    Class<?> first = classLoader.loadClass("java.lang.String", false);
    Class<?> second = classLoader.loadClass("java.lang.String", false);
    assertSame(first, second, "Класс должен кешироваться и возвращаться один и тот же экземпляр");
  }

  /** Если класс присутствует внутри URL-источников, он находится через findClass. */
  @Test
  void testFindClassInChildFirst() throws Exception {
    // Генерируем временный JAR с байтами этого тестового класса
    String className = ChildFirstURLClassLoaderTest.class.getName();
    String entryName = className.replace('.', '/') + ".class";
    Path jarFile = Files.createTempFile("testplugin", ".jar");

    try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile))) {
      JarEntry entry = new JarEntry(entryName);
      jos.putNextEntry(entry);
      try (InputStream is =
          ChildFirstURLClassLoaderTest.class.getClassLoader().getResourceAsStream(entryName)) {
        assertNotNull(is, "Не удалось получить байты тестового класса");
        jos.write(is.readAllBytes());
      }
      jos.closeEntry();
    }

    // Загружаем класс из JAR через ChildFirstURLClassLoader без родителя
    ChildFirstURLClassLoader loader =
        new ChildFirstURLClassLoader(new URL[] {jarFile.toUri().toURL()}, null);
    Class<?> loaded = loader.loadClass(className, false);
    assertEquals(className, loaded.getName());
  }
}
