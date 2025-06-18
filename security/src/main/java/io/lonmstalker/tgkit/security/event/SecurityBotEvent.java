package io.lonmstalker.tgkit.security.event;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.bot.BotRegistryImpl;
import io.lonmstalker.tgkit.core.event.TelegramBotEvent;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;

public record SecurityBotEvent(@NonNull Type type,
                               @NonNull Instant timestamp,
                               @NonNull BotRequest<?> request) implements TelegramBotEvent {

    @Override
    public long botInternalId() {
        return request.botInfo().internalId();
    }

    @Override
    public long botExternalId() {
        return BotRegistryImpl.INSTANCE.getByInternalId(botInternalId())
                .orElseThrow(() -> new BotApiException("Cannot find bot"))
                .externalId();
    }

    public enum Type {
        FLOOD,
        DUPLICATE,
        MALICIOUS_URL
    }
}
