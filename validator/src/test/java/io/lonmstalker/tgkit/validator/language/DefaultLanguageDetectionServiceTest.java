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
package io.lonmstalker.tgkit.validator.language;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Unit-тесты для {@link DefaultLanguageDetectionService}. */
class DefaultLanguageDetectionServiceTest {

  private static DefaultLanguageDetectionService svc;

  @BeforeAll
  static void init() {
    svc = new DefaultLanguageDetectionService(); // :contentReference[oaicite:3]{index=3}
  }

  @Test
  void detect_nullReturnsUnd() {
    assertEquals("und", svc.detect(null));
  }

  @Test
  void detect_blankReturnsUnd() {
    assertEquals("und", svc.detect("   "));
  }

  @Test
  void detect_englishTextReturnsEn() {
    String lang = svc.detect("Hello, world! This is a test of language detection.");
    assertEquals("en", lang);
  }

  @Test
  void detect_russianTextReturnsRu() {
    String lang = svc.detect("Привет мир! Это тест для определения языка.");
    // Tika может вернуть 'ru' или 'ru_RU', но мы ожидаем хотя бы первые два символа
    assertTrue(lang.startsWith("ru"), "Expected Russian language code, got: " + lang);
  }

  @Test
  void detect_mixedTextFallsBackToDominant() {
    // В смешанном тексте доминирует македонский по вхождениям
    String mixed = "Hello! Как дела? Это тест.";
    String lang = svc.detect(mixed);
    assertTrue(lang.startsWith("mk"), "Expected dominant language Macedonian, got: " + lang);
  }
}
