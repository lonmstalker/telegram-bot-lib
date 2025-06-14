package io.lonmstalker.core.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileBotUserRepository")
class FileBotUserRepositoryTest {

    @Test
    @DisplayName("сохраняет и возвращает пользователя")
    void saveAndGetUser() {
        FileBotUserRepository repo = new FileBotUserRepository("./test-users");
        User tgUser = new User();
        tgUser.setId(1L);

        BotUserInfo info = repo.getOrCreate(tgUser);
        assertEquals("1", info.chatId());
        assertTrue(info.roles().isEmpty());
        assertEquals(info.chatId(), repo.getOrCreate(tgUser).chatId());
    }
}
