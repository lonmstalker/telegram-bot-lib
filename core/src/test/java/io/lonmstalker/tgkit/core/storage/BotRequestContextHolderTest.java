package io.lonmstalker.tgkit.core.storage;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotRequestContextHolderTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void setAndGet() {
        Update u = new Update();
        TelegramSender sender = new TelegramSender(BotConfig.builder().build(), "token") {
        };
        BotRequestContextHolder.setUpdate(u);
        BotRequestContextHolder.setSender(sender);

        assertEquals(u, BotRequestContextHolder.getUpdate());
        assertEquals(sender, BotRequestContextHolder.getSender());
        assertEquals(u, BotRequestContextHolder.getUpdateNotNull());
        assertEquals(sender, BotRequestContextHolder.getSenderNotNull());
    }

    @Test
    void clearAndExceptions() {
        BotRequestContextHolder.clear();
        assertNull(BotRequestContextHolder.getUpdate());
        assertNull(BotRequestContextHolder.getSender());
        assertThrows(RuntimeException.class, BotRequestContextHolder::getUpdateNotNull);
        assertThrows(RuntimeException.class, BotRequestContextHolder::getSenderNotNull);
    }
}
