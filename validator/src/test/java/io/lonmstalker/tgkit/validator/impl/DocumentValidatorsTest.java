package io.lonmstalker.tgkit.validator.impl;

import static io.lonmstalker.tgkit.validator.impl.DocumentValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Document;

class DocumentValidatorsTest {

  private Document doc(long bytes, String mime) {
    Document d = new Document();
    d.setFileSize(bytes);
    d.setMimeType(mime);
    d.setFileId("doc");
    return d;
  }

  @Test
  void maxSizeMb_allowsWithinLimit() {
    assertDoesNotThrow(() -> maxSizeMb(2).validate(doc(1024 * 1024, "application/pdf")));
  }

  @Test
  void maxSizeMb_rejectsTooLarge() {
    ValidationException ex =
        assertThrows(
            ValidationException.class,
            () -> maxSizeMb(1).validate(doc(2 * 1024 * 1024, "application/pdf")));
    assertEquals("error.doc.tooLarge", ex.getErrorKey().key());
  }

  @Test
  void allowedMime_allowsKnown() {
    assertDoesNotThrow(() -> allowedMime().validate(doc(100, "application/pdf")));
  }

  @Test
  void allowedMime_rejectsUnknown() {
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> allowedMime().validate(doc(100, "application/xyz")));
    assertEquals("error.doc.mime", ex.getErrorKey().key());
  }

  @Test
  void safeContent_allowsWhenServiceUnavailable() {
    assertDoesNotThrow(() -> safeContent().validate(doc(10, "application/pdf")));
  }
}
