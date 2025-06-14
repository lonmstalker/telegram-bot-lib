package io.lonmstalker.core.bot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.matching.CommandMatch;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

class BotCommandRegistryImplTest {

    @Test
    void addAndFindCommands() {
        BotCommandRegistryImpl registry = new BotCommandRegistryImpl();
        BotCommand<Message> first = new TestCommand(1);
        BotCommand<Message> second = new TestCommand(2);
        registry.add(first);
        registry.add(second);

        Message msg = new Message();
        BotCommand<Message> found = registry.find(BotRequestType.MESSAGE, msg);
        assertEquals(first, found);
    }

    private static class TestCommand implements BotCommand<Message> {
        private final int order;
        TestCommand(int order) {this.order = order;}
        @Override public BotResponse handle(BotRequest<Message> request) {return null;}
        @Override public BotRequestType type() {return BotRequestType.MESSAGE;}
        @Override public CommandMatch<Message> matcher() {return data -> true;}
        @Override public int order() {return order;}
    }
}
