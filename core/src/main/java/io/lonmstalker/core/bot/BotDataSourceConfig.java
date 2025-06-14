package io.lonmstalker.core.bot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.sql.DataSource;

@Getter
@Builder
@AllArgsConstructor
public class BotDataSourceConfig {
    private BotConfig botConfig;
    private DataSource dataSource;
}
