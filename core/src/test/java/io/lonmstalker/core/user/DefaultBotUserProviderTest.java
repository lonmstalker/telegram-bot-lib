package io.lonmstalker.core.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DefaultBotUserProvider")
class DefaultBotUserProviderTest {

    @Test
    @DisplayName("возвращает одного и того же пользователя")
    void returnsSameUser() {
        DefaultBotUserProvider provider = new DefaultBotUserProvider("./test-users-provider");

        Update update = new Update();
        Message msg = new Message();
        User tgUser = new User();
        tgUser.setId(2L);
        msg.setFrom(tgUser);
        update.setMessage(msg);

        BotUserInfo info1 = provider.resolve(update);
        BotUserInfo info2 = provider.resolve(update);

        assertEquals(info1.chatId(), info2.chatId());
        assertEquals("2", info1.chatId());
    }
}
