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
package io.github.tgkit.validator.impl;

import static io.github.tgkit.validator.impl.TextValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.core.exception.ValidationException;
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
