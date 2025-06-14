import io.lonmstalker.core.bot.TelegramSender;
import io.lonmstalker.core.storage.BotRequestHolder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotRequestHolder")
class BotRequestHolderTest {
    @Test
    @DisplayName("хранит и очищает значения")
    void holderStoresAndClearsValues() {
        Update u = new Update();
        TelegramSender sender = new TelegramSender(new BotConfig(), "t");
        BotRequestHolder.setUpdate(u);
        BotRequestHolder.setSender(sender);
        assertEquals(u, BotRequestHolder.getUpdate());
        assertEquals(sender, BotRequestHolder.getSender());
        BotRequestHolder.clear();
        assertNull(BotRequestHolder.getUpdate());
        assertNull(BotRequestHolder.getSender());
    }
}
