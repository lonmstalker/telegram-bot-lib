package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.matching.AlwaysMatch;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.*;

class BotCommandRegistryTest {
    @Test
    void addAndFindCommand() {
        BotCommandRegistry registry = new BotCommandRegistryImpl();
        BotCommand<Message> cmd = new BotCommand<>() {
            @Override
            public BotResponse handle(BotRequest<Message> request) {
                return new BotResponse(null);
            }

            @Override
            public BotRequestType type() {
                return BotRequestType.MESSAGE;
            }

            @Override
            public AlwaysMatch<Message> matcher() {
                return new AlwaysMatch<>();
            }
        };
        registry.add(cmd);

        Message msg = new Message();
        assertEquals(cmd, registry.find(BotRequestType.MESSAGE, msg));
    }
}
