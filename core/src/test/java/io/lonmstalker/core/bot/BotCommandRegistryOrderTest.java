package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.matching.AlwaysMatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("BotCommandRegistryImpl")
class BotCommandRegistryOrderTest {

    @Test
    @DisplayName("возвращает команду с меньшим order")
    void shouldReturnCommandWithLowerOrderFirst() {
        BotCommandRegistry registry = new BotCommandRegistryImpl();

        BotCommand<BotApiObject> first = new TestCmd(1);
        BotCommand<BotApiObject> second = new TestCmd(0);
        registry.add(first);
        registry.add(second);

        assertEquals(second, registry.find(BotRequestType.MESSAGE, new Message()));
    }

    static class TestCmd implements BotCommand<BotApiObject> {
        private final int ord;
        TestCmd(int ord) { this.ord = ord; }
        @Override
        public BotResponse handle(BotRequest<BotApiObject> request) { return new BotResponse(); }
        @Override
        public BotRequestType type() { return BotRequestType.MESSAGE; }
        @Override
        public AlwaysMatch<BotApiObject> matcher() { return new AlwaysMatch<>(); }
        @Override
        public int order() { return ord; }
    }

}
