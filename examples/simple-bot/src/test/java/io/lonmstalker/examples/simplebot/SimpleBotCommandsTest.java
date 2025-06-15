package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.tgkit.core.bot.BotRequestConverterImpl;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.loader.AnnotatedCommandLoader;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.utils.UpdateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import static org.junit.jupiter.api.Assertions.*;

class SimpleBotCommandsTest {
    private BotCommandRegistryImpl registry;
    private BotRequestConverterImpl converter;
    private SimpleRoleProvider provider;

    @BeforeEach
    void setUp() {
        registry = new BotCommandRegistryImpl();
        converter = new BotRequestConverterImpl();
        provider = new SimpleRoleProvider();
        AnnotatedCommandLoader.load(registry, "io.lonmstalker.examples.simplebot");
    }

    @Test
    @DisplayName("тест MessageTextMatch")
    void textPing() {
        User user = new User();
        Chat chat = new Chat();
        Message msg = new Message();

        chat.setId(1L);
        user.setId(1L);

        msg.setFrom(user);
        msg.setChat(chat);
        msg.setText("ping");

        Update update = new Update();
        update.setMessage(msg);

        assertMethodType(update, SendMessage.class);
    }

    @Test
    @DisplayName("тест MessageContainsMatch")
    void containsHello() {
        Chat chat = new Chat();
        User user = new User();
        Message msg = new Message();

        user.setId(1L);
        chat.setId(1L);

        msg.setText("hello there");
        msg.setChat(chat);
        msg.setFrom(user);

        Update update = new Update();
        update.setMessage(msg);

        assertMethodType(update, SendMessage.class);
    }

    @Test
    @DisplayName("тест MessageRegexMatch")
    void regexNumbers() {
        User user = new User();
        Chat chat = new Chat();
        Message msg = new Message();

        user.setId(1L);
        chat.setId(1L);

        msg.setText("abc123");
        msg.setChat(chat);
        msg.setFrom(user);

        Update update = new Update();
        update.setMessage(msg);

        assertMethodType(update, SendMessage.class);
    }

    @Test
    @DisplayName("тест UserRoleMatch")
    void callback() {
        User user = new User();
        CallbackQuery cq = new CallbackQuery();

        user.setId(1L);

        cq.setId("1");
        cq.setFrom(user);

        Update update = new Update();
        update.setCallbackQuery(cq);

        assertMethodType(update, AnswerCallbackQuery.class);
    }

    @Test
    @DisplayName("тест AlwaysMatch")
    void inlineQuery() {
        User user = new User();
        InlineQuery iq = new InlineQuery();

        user.setId(1L);

        iq.setId("1");
        iq.setFrom(user);

        Update update = new Update();
        update.setInlineQuery(iq);

        assertMethodType(update, AnswerInlineQuery.class);
    }

    private void assertMethodType(Update update, Class<?> expected) {
        BotRequestType type = UpdateUtils.getType(update);

        var data = converter.convert(update, type);
        var command = registry.find(type, "", data);
        assertNotNull(command);

        var user = provider.resolve(update);
        var localizer = new MessageLocalizer(java.util.Locale.getDefault());
        BotInfo info = new BotInfo(1L, new InMemoryStateStore(), new TestSender(), localizer);
        BotResponse resp = command.handle(new BotRequest<>(1, data, info, user));

        assertNotNull(resp);
        assertNotNull(resp.getMethod());
        assertEquals(expected, resp.getMethod().getClass());
    }

    static class TestSender extends TelegramSender {
        TestSender() {
            super(BotConfig.builder().build(), "TOKEN");
        }
        @Override
        public void close() {
        }
    }
}
