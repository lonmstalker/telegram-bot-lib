package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

/** Настройки по умолчанию для BotResponse. */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DslGlobalConfig {

    /** Глобальная конфигурация. */
    public static final DslGlobalConfig INSTANCE = new DslGlobalConfig();

    private boolean sanitize;
    private @NonNull ParseMode parseMode = ParseMode.HTML;
    private @NonNull FeatureFlags flags = FeatureFlags.noop();

    private @NonNull TelegramTransport transport = new TelegramTransport() {
        @Override
        public long execute(@NonNull PartialBotApiMethod<?> method) {
            log.debug("execute: {}", method); return 0;
        }
        @Override
        public void delete(long chatId, long messageId) {
            log.debug("delete: {}", messageId);
        }
    };

    public @NonNull DslGlobalConfig markdownV2() {
        this.parseMode = ParseMode.MARKDOWN_V2;
        return this;
    }

    public @NonNull DslGlobalConfig sanitizeMarkdown() {
        this.sanitize = true;
        return this;
    }

    public @NonNull DslGlobalConfig featureFlags(@NonNull FeatureFlags flags) {
        this.flags = flags;
        return this;
    }

    public @NonNull DslGlobalConfig transport(@NonNull TelegramTransport tr) {
        this.transport = tr;
        return this;
    }
}
