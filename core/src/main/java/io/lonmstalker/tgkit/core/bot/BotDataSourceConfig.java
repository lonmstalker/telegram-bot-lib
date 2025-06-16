package io.lonmstalker.tgkit.core.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.sql.DataSource;

@Getter
@Builder
@AllArgsConstructor
public class BotDataSourceConfig {
    private @Nullable BotConfig botConfig;
    private @NonNull DataSource dataSource;
}
