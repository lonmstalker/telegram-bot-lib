package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizerImpl;
import org.checkerframework.checker.nullness.qual.NonNull;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.MessageRegexMatch;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.loader.AnnotatedCommandLoader;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
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
        @Override public @NonNull Set<String> roles() { return Set.of(); }
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
                new TelegramSender(BotConfig.builder().build(), "T"),
                new MessageLocalizerImpl("i18n/messages", Locale.US));
        BotRequest<Message> req = new BotRequest<>(0, msg, info, new User("1"), Locale.getDefault());
        cmd.handle(req);

        assertEquals(42, Commands.captured);
    }
}
