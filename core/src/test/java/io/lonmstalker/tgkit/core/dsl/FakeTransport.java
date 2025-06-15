package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;

/** Простейшая реализация транспорта для тестов. */
public final class FakeTransport implements TelegramTransport {
    public final List<BotApiMethod<?>> sent = new ArrayList<>();
    public final List<Long> deleted = new ArrayList<>();
    public final List<Duration> ttls = new ArrayList<>();

    @Override
    public long execute(BotApiMethod<?> method) {
        sent.add(method);
        return sent.size();
    }

    @Override
    public void delete(long chatId, long messageId) {
        deleted.add(messageId);
    }

    @Override
    public void scheduleDelete(long chatId, long messageId, Duration ttl) {
        ttls.add(ttl);
    }
}
