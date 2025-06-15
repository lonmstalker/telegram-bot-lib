package io.lonmstalker.core.args;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotConfig;
import io.lonmstalker.core.BotInfo;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import io.lonmstalker.core.annotation.Arg;
import io.lonmstalker.core.annotation.BotHandler;
import io.lonmstalker.core.annotation.MessageRegexMatch;
import io.lonmstalker.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.core.i18n.MessageLocalizer;
import io.lonmstalker.core.loader.AnnotatedCommandLoader;
import io.lonmstalker.core.state.InMemoryStateStore;
import io.lonmstalker.core.user.BotUserInfo;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Locale;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ArgBindingTest {

    public static class Commands {
        static volatile int captured;

        @BotHandler(type = BotRequestType.MESSAGE)
        @MessageRegexMatch("id (?<id>\\d+)")
        public BotResponse test(BotRequest<Message> req, @Arg("id") int id) {
            captured = id;
            return null;
        }
    }

    private record User(String chatId) implements BotUserInfo {
        @Override public Set<String> roles() { return Set.of(); }
    }

    @Test
    void bindArgument() {
        BotCommandRegistryImpl reg = new BotCommandRegistryImpl();
        AnnotatedCommandLoader.load(reg, Commands.class.getPackageName());

        Message msg = new Message();
        msg.setText("id 42");
        var cmd = reg.find(BotRequestType.MESSAGE, "", msg);
        assertNotNull(cmd);

        BotInfo info = new BotInfo(1L, new InMemoryStateStore(),
                new io.lonmstalker.core.bot.TelegramSender(new BotConfig(), "T"),
                new MessageLocalizer(Locale.US));
        BotRequest<Message> req = new BotRequest<>(0, msg, info, new User("1"));
        ((BotCommand<Message>) cmd).handle((BotRequest<BotApiObject>)(BotRequest<?>)req);

        assertEquals(42, Commands.captured);
    }
}
