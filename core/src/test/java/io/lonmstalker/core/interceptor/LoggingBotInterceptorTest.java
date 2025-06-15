package io.lonmstalker.core.interceptor;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.interceptor.defaults.LoggingBotInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static org.junit.jupiter.api.Assertions.*;

public class LoggingBotInterceptorTest {
    private final Logger logger = (Logger) LoggerFactory.getLogger(LoggingBotInterceptor.class);
    private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
    }

    @Test
    void logs_user_and_message_id_on_error() {
        appender.start();
        logger.addAppender(appender);
        LoggingBotInterceptor interceptor = new LoggingBotInterceptor();
        Update update = new Update();
        Message msg = new Message();
        msg.setMessageId(5);
        User user = new User();
        user.setId(10L);
        msg.setFrom(user);
        update.setMessage(msg);
        interceptor.afterCompletion(update, (BotResponse) null, new RuntimeException("boom"));
        assertFalse(appender.list.isEmpty());
        ILoggingEvent event = appender.list.get(0);
        assertEquals(Level.DEBUG, event.getLevel());
        Update logged = (Update) event.getArgumentArray()[0];
        assertEquals(5, logged.getMessage().getMessageId());
        assertEquals(10L, logged.getMessage().getFrom().getId());
    }
}
