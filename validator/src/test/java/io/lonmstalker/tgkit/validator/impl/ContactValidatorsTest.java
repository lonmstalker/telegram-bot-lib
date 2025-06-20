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
