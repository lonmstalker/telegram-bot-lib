package io.lonmstalker.tgkit.core.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.*;

public class UpdateUtilsTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void getTypeMessage() {
        Update update = new Update();
        update.setMessage(new Message());
        assertEquals(BotRequestType.MESSAGE, UpdateUtils.getType(update));
    }

    @Test
    void getTypeCallbackQuery() {
        Update update = new Update();
        update.setCallbackQuery(new CallbackQuery());
        assertEquals(BotRequestType.CALLBACK_QUERY, UpdateUtils.getType(update));
    }

    @Test
    void getTypeUnknownThrows() {
        Update update = new Update();
        assertThrows(BotApiException.class, () -> UpdateUtils.getType(update));
    }

    @Test
    void getUserFromCallback() {
        User user = new User();
        user.setId(1L);
        CallbackQuery cq = new CallbackQuery();
        cq.setFrom(user);
        Update update = new Update();
        update.setCallbackQuery(cq);
        assertEquals(user, UpdateUtils.getUser(update));
    }
}
