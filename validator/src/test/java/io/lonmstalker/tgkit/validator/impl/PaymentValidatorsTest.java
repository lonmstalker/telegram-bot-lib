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

import static io.lonmstalker.tgkit.validator.impl.PaymentValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.payments.Invoice;

class PaymentValidatorsTest {

  private Invoice invoice(int cents, String currency) {
    Invoice i = new Invoice();
    i.setTotalAmount(cents);
    i.setCurrency(currency);
    return i;
  }

  @Test
  void validAmount_acceptsPositive() {
    assertDoesNotThrow(() -> validAmount().validate(invoice(100, "USD")));
  }

  @Test
  void validAmount_rejectsZero() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> validAmount().validate(invoice(0, "USD")));
    assertEquals("error.payment.amount", ex.getErrorKey().key());
  }

  @Test
  void validCurrency_acceptsIso() {
    assertDoesNotThrow(() -> validCurrency().validate(invoice(100, "EUR")));
  }

  @Test
  void validCurrency_rejectsBad() {
    ValidationException ex =
        assertThrows(
            ValidationException.class, () -> validCurrency().validate(invoice(100, "EURO")));
    assertEquals("error.payment.currency", ex.getErrorKey().key());
  }
}
