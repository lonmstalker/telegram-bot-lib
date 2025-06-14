package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.utils.UpdateUtils;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUtilsTest {
    @Test
    void shouldDetectMessageTypeAndExtractUser() {
        Update update = new Update();
        update.setUpdateId(1);
        Message msg = new Message();
        User user = new User();
        user.setId(1L);
        user.setFirstName("name");
        msg.setFrom(user);
        update.setMessage(msg);

        assertEquals(BotRequestType.MESSAGE, UpdateUtils.getType(update));
        assertEquals(user, UpdateUtils.getUser(update));
    }
}
