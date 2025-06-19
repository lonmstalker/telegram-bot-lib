package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.payments.Invoice;

import static io.lonmstalker.tgkit.validator.impl.PaymentValidators.*;
import static org.junit.jupiter.api.Assertions.*;

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
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validAmount().validate(invoice(0, "USD"))
        );
        assertEquals("error.payment.amount", ex.getErrorKey().key());
    }

    @Test
    void validCurrency_acceptsIso() {
        assertDoesNotThrow(() -> validCurrency().validate(invoice(100, "EUR")));
    }

    @Test
    void validCurrency_rejectsBad() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validCurrency().validate(invoice(100, "EURO"))
        );
        assertEquals("error.payment.currency", ex.getErrorKey().key());
    }
}
