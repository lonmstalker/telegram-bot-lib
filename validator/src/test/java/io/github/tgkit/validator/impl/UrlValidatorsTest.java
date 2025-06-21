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

import static io.github.tgkit.validator.impl.UrlValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.internal.exception.ValidationException;
import org.junit.jupiter.api.Test;

class UrlValidatorsTest {

  @Test
  void validUri_acceptsWellFormed() {
    assertDoesNotThrow(() -> validUri().validate("https://example.com/path"));
  }

  @Test
  void validUri_rejectsMalformed() {
    // "://nohost" — у URI.create(...) точно вызовет IllegalArgumentException
    ValidationException ex =
        assertThrows(ValidationException.class, () -> validUri().validate("://nohost"));
    assertEquals("error.url.syntax", ex.getErrorKey().key());
  }

  @Test
  void safeBrowsing_allowsWhenServiceUnavailable() {
    assertDoesNotThrow(() -> safeBrowsing().validate("https://example.com"));
  }
}
