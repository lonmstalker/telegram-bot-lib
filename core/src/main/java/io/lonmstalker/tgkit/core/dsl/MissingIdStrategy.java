package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FunctionalInterface
@SuppressWarnings("argument")
public interface MissingIdStrategy {
    Logger LOG = LoggerFactory.getLogger(MissingIdStrategy.class);

    /**
     * Вызывается, когда chatId или userId отсутствуют.
     */
    void onMissing(String idName, DSLContext ctx) throws BotApiException;

    /**
     * --- Готовые стратегии ---
     */
    MissingIdStrategy ERROR = (name, u) -> {
        throw new BotApiException(name + " is required but null in update: " + BotRequestHolder.getRequestId());
    };
    MissingIdStrategy WARN = (name, u) -> LOG.warn("{} is null in update {}", name,
            BotRequestHolder.getRequestId());
    MissingIdStrategy IGNORE = (name, u) -> { /* ничего */ };
}
