package io.lonmstalker.tgkit.core.args;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ConvertersTest {
    @Test
    void shouldThrowWhenNumberConverterUnsupported() {
        var converter = Converters.getByType(BigDecimal.class);
        assertThrows(BotApiException.class,
                () -> converter.convert("1", new Context(null, null)));
    }
}
