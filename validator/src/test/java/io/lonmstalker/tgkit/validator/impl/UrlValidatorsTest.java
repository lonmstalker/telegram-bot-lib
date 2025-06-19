package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;

import static io.lonmstalker.tgkit.validator.impl.UrlValidators.*;
import static org.junit.jupiter.api.Assertions.*;

class UrlValidatorsTest {

    @Test
    void validUri_acceptsWellFormed() {
        assertDoesNotThrow(() -> validUri().validate("https://example.com/path"));
    }

    @Test
    void validUri_rejectsMalformed() {
        // "://nohost" — у URI.create(...) точно вызовет IllegalArgumentException
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> validUri().validate("://nohost")
        );
        assertEquals("error.url.syntax", ex.getErrorKey().key());
    }

    @Test
    void safeBrowsing_allowsWhenServiceUnavailable() {
        assertDoesNotThrow(() -> safeBrowsing().validate("https://example.com"));
    }
}
