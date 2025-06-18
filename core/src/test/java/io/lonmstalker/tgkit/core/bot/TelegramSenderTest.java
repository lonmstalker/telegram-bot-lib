package io.lonmstalker.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.lang.reflect.Method;

import java.lang.reflect.InvocationTargetException;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;

public class TelegramSenderTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void convertException() throws Exception {
        TestSender sender = new TestSender();
        Method m = TelegramSender.class.getDeclaredMethod("withConvertException", TelegramSender.RuntimeExceptionExecutor.class);
        m.setAccessible(true);
        TelegramSender.RuntimeExceptionExecutor<String> exec = () -> { throw new Exception("boom"); };
        var ex = assertThrows(InvocationTargetException.class, () -> m.invoke(sender, exec));
        assertTrue(ex.getCause() instanceof BotApiException);
        assertEquals("boom", ex.getCause().getCause().getMessage());
    }

    static class TestSender extends TelegramSender {
        TestSender() { super(BotConfig.builder().build(), "token"); }
    }
}
