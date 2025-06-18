package io.lonmstalker.tgkit.core.args;

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class ConvertersTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void unsupported_number_converter() {
        var converter = Converters.getByType(BigDecimal.class);
        assertThrows(BotApiException.class,
                () -> converter.convert("1", new Context(null, null)));
    }
}
