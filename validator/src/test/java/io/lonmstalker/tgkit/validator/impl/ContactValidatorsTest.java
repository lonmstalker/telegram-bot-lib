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
package io.lonmstalker.tgkit.validator.impl;

import static io.lonmstalker.tgkit.validator.impl.ContactValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Contact;

class ContactValidatorsTest {

  private Contact contact(String phone, String name) {
    Contact c = new Contact();
    c.setPhoneNumber(phone);
    c.setFirstName(name);
    return c;
  }

  @Test
  void validPhone_acceptsE164() {
    assertDoesNotThrow(() -> validPhone().validate(contact("+1234567890", "A")));
  }

  @Test
  void validPhone_rejectsBadFormat() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> validPhone().validate(contact("12345", "A")));
    assertEquals("error.contact.phone", ex.getErrorKey().key());
  }

  @Test
  void validName_acceptsShort() {
    assertDoesNotThrow(() -> validName().validate(contact("+1", "Bob")));
  }

  @Test
  void validName_rejectsTooLong() {
    String longName = "x".repeat(300);
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> validName().validate(contact("+1", longName)));
    assertEquals("error.contact.name", ex.getErrorKey().key());
  }
}
