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

import static io.lonmstalker.tgkit.validator.impl.AdvancedValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

class AdvancedValidatorsTest {

  @Test
  void noForeignLinks_allowsAllowedDomain() {
    String txt = "See https://good.com/page";
    assertDoesNotThrow(() -> noForeignLinks(Set.of("good.com")).validate(txt));
  }

  @Test
  void noForeignLinks_rejectsForbiddenDomain() {
    ValidationException ex =
        assertThrows(
            ValidationException.class,
            () -> noForeignLinks(Set.of("good.com")).validate("http://bad.com"));
    assertEquals("error.link.forbidden", ex.getErrorKey().key());
  }

  @Test
  void currencyPair_acceptsValid() {
    assertDoesNotThrow(() -> currencyPair(Set.of("USD", "EUR")).validate("10.00 USD"));
  }

  @Test
  void currencyPair_rejectsInvalid() {
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> currencyPair(Set.of("USD")).validate("ten USD"));
    assertEquals("error.currency.invalid", ex.getErrorKey().key());
  }

  @Test
  void futureDate_acceptsTodayAndAhead() {
    String today = LocalDate.now(ZoneOffset.UTC).toString();
    assertDoesNotThrow(() -> futureDate(5).validate(today));
  }

  @Test
  void futureDate_rejectsPastOrTooFar() {
    String past = LocalDate.now(ZoneOffset.UTC).minusDays(1).toString();
    ValidationException ex1 =
        assertThrows(ValidationException.class, () -> futureDate(3).validate(past));
    assertEquals("error.date.invalidOrOutOfRange", ex1.getErrorKey().key());

    String far = LocalDate.now(ZoneOffset.UTC).plusDays(10).toString();
    ValidationException ex2 =
        assertThrows(ValidationException.class, () -> futureDate(5).validate(far));
    assertEquals("error.date.invalidOrOutOfRange", ex2.getErrorKey().key());
  }

  @Test
  void timestampDrift_acceptsWithinDrift() {
    Message m = new Message();
    m.setDate((int) Instant.now().getEpochSecond());
    assertDoesNotThrow(() -> timestampDrift(5).validate(m));
  }

  @Test
  void timestampDrift_rejectsBeyondDrift() {
    Message m = new Message();
    m.setDate((int) (Instant.now().getEpochSecond() - 10));
    ValidationException ex =
        assertThrows(ValidationException.class, () -> timestampDrift(5).validate(m));
    assertEquals("error.message.clockDrift", ex.getErrorKey().key());
  }
}
