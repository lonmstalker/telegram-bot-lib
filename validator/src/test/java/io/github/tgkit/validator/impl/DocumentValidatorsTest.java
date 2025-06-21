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

import static io.github.tgkit.validator.impl.DocumentValidators.*;
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
