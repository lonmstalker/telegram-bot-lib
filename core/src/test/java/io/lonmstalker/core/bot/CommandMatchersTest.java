import io.lonmstalker.core.matching.*;
import io.lonmstalker.core.storage.BotRequestHolder;
import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CommandMatch implementations")
class CommandMatchersTest {
    @AfterEach
    void cleanup() {
        BotRequestHolder.clear();
    }

    @Test
    @DisplayName("MessageTextMatch")
    void messageTextMatch() {
        Message msg = new Message();
        msg.setText("Hello");
        assertTrue(new MessageTextMatch("Hello").match(msg));
        assertFalse(new MessageTextMatch("hello").match(msg));
        assertTrue(new MessageTextMatch("hello", true).match(msg));
    }

    @Test
    @DisplayName("MessageContainsMatch")
    void messageContainsMatch() {
        Message msg = new Message();
        msg.setText("Hello world");
        assertTrue(new MessageContainsMatch("world").match(msg));
        assertFalse(new MessageContainsMatch("WORLD").match(msg));
        assertTrue(new MessageContainsMatch("WORLD", true).match(msg));
    }

    @Test
    @DisplayName("MessageRegexMatch")
    void messageRegexMatch() {
        Message msg = new Message();
        msg.setText("abc123");
        assertTrue(new MessageRegexMatch("\\w+\\d+").match(msg));
    }

    @Test
    @DisplayName("AlwaysMatch")
    void alwaysMatch() {
        Message msg = new Message();
        assertTrue(new AlwaysMatch<>().match(msg));
    }

    @Test
    @DisplayName("UserRoleMatch")
    void userRoleMatch() {
        Update update = new Update();
        BotRequestHolder.setUpdate(update);
        BotUserInfo info = mock(BotUserInfo.class);
        when(info.roles()).thenReturn(Set.of("ADMIN"));
        BotUserProvider provider = mock(BotUserProvider.class);
        when(provider.resolve(update)).thenReturn(info);
        CommandMatch<BotApiObject> match = new UserRoleMatch<>(provider, Set.of("ADMIN"));
        assertTrue(match.match(msg()));
        assertFalse(new UserRoleMatch<>(provider, Set.of("USER")).match(msg()));
    }

    private Message msg() {
        return new Message();
    }
}
