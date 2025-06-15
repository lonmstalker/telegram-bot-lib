package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/** Простейшая реализация транспорта для тестов. */
public final class FakeTransport implements TelegramTransport {
    public final List<Long> deleted = new ArrayList<>();
    public final List<Duration> ttls = new ArrayList<>();
    public final List<PartialBotApiMethod<?>> sent = new ArrayList<>();

    @Override
    public long execute(@NonNull PartialBotApiMethod<?> method) {
        sent.add(method);
        return sent.size();
    }

    @Override
    public void delete(long chatId, long messageId) {
        deleted.add(messageId);
    }

    @Override
    public void scheduleDelete(long chatId, long messageId, @NonNull Duration ttl) {
        ttls.add(ttl);
    }
}
