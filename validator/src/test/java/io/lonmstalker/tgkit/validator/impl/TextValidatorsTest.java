package io.lonmstalker.tgkit.validator.impl;

import static io.lonmstalker.tgkit.validator.impl.TextValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;

class TextValidatorsTest {

  @Test
  void notBlank_allowsNonBlank() {
    assertDoesNotThrow(() -> notBlank().validate("hello"));
  }

  @Test
  void notBlank_rejectsBlank() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> notBlank().validate("   "));
    assertEquals("error.text.blank", ex.getErrorKey().key());
  }

  @Test
  void maxLength_allowsWithinLimit() {
    String input = "x".repeat(4096);
    assertDoesNotThrow(() -> maxLength().validate(input));
  }

  @Test
  void maxLength_rejectsTooLong() {
    String input = "x".repeat(4097);
    ValidationException ex =
        assertThrows(ValidationException.class, () -> maxLength().validate(input));
    assertEquals("error.text.tooLong", ex.getErrorKey().key());
  }
}
