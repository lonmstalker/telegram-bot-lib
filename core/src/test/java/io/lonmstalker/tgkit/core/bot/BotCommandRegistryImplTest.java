package io.lonmstalker.tgkit.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import io.lonmstalker.tgkit.core.exception.BotApiException;

class BotCommandRegistryImplTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void add_and_find_commands() {
        BotCommandRegistryImpl registry = new BotCommandRegistryImpl();
        BotCommand<Message> first = new TestCommand(1);
        BotCommand<Message> second = new TestCommand(2);

        registry.add(first);
        registry.add(second);

        Message msg = new Message();
        BotCommand<Message> found = registry.find(BotRequestType.MESSAGE, "", msg);
        assertEquals(first, found);
    }

    @Test
    void find_wrong_type_throws() {
        BotCommandRegistryImpl registry = new BotCommandRegistryImpl();
        registry.add(new TestCommand(1));

        Update update = new Update();
        assertThrows(BotApiException.class,
                () -> registry.find(BotRequestType.MESSAGE, "", update));
    }

    private static class TestCommand implements BotCommand<Message> {
        private final int order;

        TestCommand(int order) {
            this.order = order;
        }

        @Override
        public BotResponse handle(@NonNull BotRequest<Message> request) { return null; }

        @Override
        public @NonNull BotRequestType type() { return BotRequestType.MESSAGE; }

        @Override
        public @NonNull CommandMatch<Message> matcher() { return data -> true; }

        @Override
        public int order() { return order; }

        @Override
        public @NonNull String botGroup() { return ""; }
    }
}
