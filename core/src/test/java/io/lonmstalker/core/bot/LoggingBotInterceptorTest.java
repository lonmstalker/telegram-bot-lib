package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.interceptor.defaults.LoggingBotInterceptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("LoggingBotInterceptor")
class LoggingBotInterceptorTest {

    @Test
    @DisplayName("вызывает методы без исключений")
    void shouldRunWithoutExceptions() {
        LoggingBotInterceptor i = new LoggingBotInterceptor();
        Update u = new Update();
        assertDoesNotThrow(() -> i.preHandle(u));
        assertDoesNotThrow(() -> i.postHandle(u));
        assertDoesNotThrow(() -> i.afterCompletion(u, new BotResponse()));
    }
}
