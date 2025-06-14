import io.lonmstalker.core.bot.*;
import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotImpl extra logic")
class BotImplExtraTest {
    static class DummySender extends TelegramSender {
        DummySender() { super(new DefaultBotOptions(), "t"); }
    }

    @Test
    @DisplayName("stop до start генерирует исключение")
    void stopBeforeStartShouldThrow() {
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("t")
                .config(new BotConfig())
                .absSender(new DummySender())
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        assertThrows(BotApiException.class, bot::stop);
    }

    @Test
    @DisplayName("stop после start отрабатывает")
    void stopAfterStartWorks() throws Exception {
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("t")
                .config(new BotConfig())
                .absSender(new DummySender() {
                    @Override
                    public <T extends java.io.Serializable, Method extends org.telegram.telegrambots.meta.api.methods.BotApiMethod<T>> T sendApiMethod(Method method) {
                        User u = new User();
                        u.setId(2L);
                        u.setUserName("u");
                        return (T) u;
                    }
                })
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.start();
        assertDoesNotThrow(bot::stop);
    }

    @Test
    @DisplayName("onComplete добавляет действие")
    void onCompleteAddsAction() throws Exception {
        BotImpl bot = BotImpl.builder()
                .id(1)
                .token("t")
                .config(new BotConfig())
                .absSender(new DummySender())
                .commandRegistry(new BotCommandRegistryImpl())
                .build();
        bot.onComplete(() -> {});
        Field f = BotImpl.class.getDeclaredField("completeActions");
        f.setAccessible(true);
        var list = (java.util.List<?>) f.get(bot);
        assertEquals(1, list.size());
    }
}
