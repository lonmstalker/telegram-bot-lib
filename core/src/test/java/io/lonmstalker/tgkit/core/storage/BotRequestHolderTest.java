package io.lonmstalker.tgkit.core.storage;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotRequestHolderTest {

    @Test
    void setAndGet() {
        Update u = new Update();
        TelegramSender sender = new TelegramSender(BotConfig.builder().build(), "token") {
        };
        BotRequestHolder.setUpdate(u);
        BotRequestHolder.setSender(sender);

        assertEquals(u, BotRequestHolder.getUpdate());
        assertEquals(sender, BotRequestHolder.getSender());
        assertEquals(u, BotRequestHolder.getUpdateNotNull());
        assertEquals(sender, BotRequestHolder.getSenderNotNull());
        sender.close();
    }

    @Test
    void clearAndExceptions() {
        BotRequestHolder.clear();
        assertNull(BotRequestHolder.getUpdate());
        assertNull(BotRequestHolder.getSender());
        assertThrows(RuntimeException.class, BotRequestHolder::getUpdateNotNull);
        assertThrows(RuntimeException.class, BotRequestHolder::getSenderNotNull);
    }
}
