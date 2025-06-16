package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.dsl.feature_flags.InMemoryFeatureFlags;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Настройки по умолчанию для BotResponse.
 */
@Slf4j
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DslGlobalConfig {

    /**
     * Глобальная конфигурация.
     */
    public static final DslGlobalConfig INSTANCE = new DslGlobalConfig();

    private volatile boolean sanitize;
    private volatile @NonNull ParseMode parseMode = ParseMode.HTML;
    private volatile @NonNull FeatureFlags flags = new InMemoryFeatureFlags();
    private volatile @NonNull MissingIdStrategy missingIdStrategy = MissingIdStrategy.ERROR;

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

    public @NonNull DslGlobalConfig missingIdStrategy(@NonNull MissingIdStrategy strategy) {
        this.missingIdStrategy = strategy;
        return this;
    }
}
