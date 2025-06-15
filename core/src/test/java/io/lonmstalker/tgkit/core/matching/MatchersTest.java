package io.lonmstalker.tgkit.core.matching;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

public class MatchersTest {

    @AfterEach
    void clear() { BotRequestHolder.clear(); }

    @Test
    void shouldMatchWhenMessageTextMatches() {
        Message msg = new Message();
        msg.setText("hello");
        assertTrue(new MessageTextMatch("hello").match(msg));
        assertFalse(new MessageTextMatch("HELLO").match(msg));
        assertTrue(new MessageTextMatch("HELLO", true).match(msg));
    }

    @Test
    void shouldMatchWhenMessageContainsText() {
        Message msg = new Message();
        msg.setText("hello world");
        assertTrue(new MessageContainsMatch("world").match(msg));
        assertFalse(new MessageContainsMatch("WORLD").match(msg));
        assertTrue(new MessageContainsMatch("WORLD", true).match(msg));
    }

    @Test
    void shouldMatchWhenMessageMatchesRegex() {
        Message msg = new Message();
        msg.setText("abc123");
        assertTrue(new MessageRegexMatch("[a-z]+\\d+").match(msg));
        assertFalse(new MessageRegexMatch("^123").match(msg));
    }

    @Test
    void shouldAlwaysMatchWhenCalled() {
        BotApiObject obj = Mockito.mock(BotApiObject.class);
        assertTrue(new AlwaysMatch<>().match(obj));
    }

    @Test
    void shouldMatchWhenUserRoleAllowed() {
        BotUserProvider provider = u -> new BotUserInfo() {
            @Override public @NonNull String chatId() { return "1"; }
            @Override public @NonNull Set<String> roles() { return Set.of("ADMIN"); }
            @Override public java.util.Locale locale() { return null; }
        };
        User tgUser = new User();
        tgUser.setId(1L);
        Update update = new Update();
        update.setMessage(new Message());
        update.getMessage().setFrom(tgUser);
        BotRequestHolder.setUpdate(update);
        UserRoleMatch<Message> match = new UserRoleMatch<>(provider, Set.of("ADMIN"));
        assertTrue(match.match(update.getMessage()));
        UserRoleMatch<Message> matchFalse = new UserRoleMatch<>(provider, Set.of("USER"));
        assertFalse(matchFalse.match(update.getMessage()));
    }
}
