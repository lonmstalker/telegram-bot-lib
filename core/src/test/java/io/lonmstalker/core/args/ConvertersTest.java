package io.lonmstalker.core.args;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ConvertersTest {
    @Test
    void unsupported_number_converter() {
        var converter = Converters.getByType(BigDecimal.class);
        assertThrows(BotApiException.class,
                () -> converter.convert("1", new Context(null, null)));
    }
}
