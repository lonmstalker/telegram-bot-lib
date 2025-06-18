package io.lonmstalker.tgkit.core.event;

import java.time.Instant;

public interface TelegramBotEvent extends BotEvent{

    long botInternalId();

    long botExternalId();

    Instant timestamp();
}
