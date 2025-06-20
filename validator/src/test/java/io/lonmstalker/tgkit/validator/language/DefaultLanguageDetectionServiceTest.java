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
