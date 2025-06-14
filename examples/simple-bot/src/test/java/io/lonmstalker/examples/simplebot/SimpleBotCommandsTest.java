package io.lonmstalker.examples.simplebot;

import io.lonmstalker.core.BotInfo;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.core.bot.BotRequestConverterImpl;
import io.lonmstalker.core.bot.TelegramSender;
import io.lonmstalker.core.loader.AnnotatedCommandLoader;
import io.lonmstalker.core.state.InMemoryStateStore;
import io.lonmstalker.core.utils.UpdateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


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
    void textPing() {
        Message msg = new Message();
        msg.setText("ping");
        msg.setChatId(1L);
        Update update = new Update();
        update.setMessage(msg);
        assertMethodType(update, SendMessage.class);
    }

    @Test
    void containsHello() {
        Message msg = new Message();
        msg.setText("hello there");
        msg.setChatId(1L);
        Update update = new Update();
        update.setMessage(msg);
        assertMethodType(update, SendMessage.class);
    }

    @Test
    void regexNumbers() {
        Message msg = new Message();
        msg.setText("abc123");
        msg.setChatId(1L);
        Update update = new Update();
        update.setMessage(msg);
        assertMethodType(update, SendMessage.class);
    }

    @Test
    void callback() {
        CallbackQuery cq = new CallbackQuery();
        cq.setId("1");
        Update update = new Update();
        update.setCallbackQuery(cq);
        assertMethodType(update, AnswerCallbackQuery.class);
    }

    @Test
    void inlineQuery() {
        InlineQuery iq = new InlineQuery();
        iq.setId("1");
        Update update = new Update();
        update.setInlineQuery(iq);
        assertMethodType(update, AnswerInlineQuery.class);
    }

    private void assertMethodType(Update update, Class<?> expected) {
        BotRequestType type = UpdateUtils.getType(update);
        var data = converter.convert(update, type);
        var command = registry.find(type, "", data);
        assertNotNull(command);
        BotInfo info = new BotInfo(1L, new TestSender(), new InMemoryStateStore());
        var user = provider.resolve(update);
        BotResponse resp = command.handle(new BotRequest<>(1, data, info, user));
        assertNotNull(resp);
        assertNotNull(resp.getMethod());
        assertEquals(expected, resp.getMethod().getClass());
    }

    static class TestSender extends TelegramSender {
        TestSender() {
            super(new io.lonmstalker.core.bot.BotConfig(), "TOKEN");
        }
        @Override
        public void close() {
        }
    }
}
