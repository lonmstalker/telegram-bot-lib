package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleRoleProviderTest {

    @Test
    void resolveFailsWhenUserMissing() {
        SimpleRoleProvider provider = new SimpleRoleProvider();
        Update update = new Update();
        assertThrows(BotApiException.class, () -> provider.resolve(update));
    }
}
