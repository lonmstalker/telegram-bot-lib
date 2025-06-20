package io.lonmstalker.tgkit.core.bot;

import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Getter
@Builder
@AllArgsConstructor
public class BotDataSourceConfig {
  private @Nullable BotConfig botConfig;
  private @NonNull DataSource dataSource;

  private BotDataSourceConfig(@Nullable BotConfig botConfig, @NonNull DataSource dataSource) {
    this.botConfig = botConfig;
    this.dataSource = dataSource;
  }

  public @Nullable BotConfig getBotConfig() {
    return botConfig;
  }

  public @NonNull DataSource getDataSource() {
    return dataSource;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private BotConfig botConfig;
    private DataSource dataSource;

    public Builder botConfig(@Nullable BotConfig botConfig) {
      this.botConfig = botConfig;
      return this;
    }

    public Builder dataSource(@NonNull DataSource dataSource) {
      this.dataSource = dataSource;
      return this;
    }

    public BotDataSourceConfig build() {
      return new BotDataSourceConfig(botConfig, dataSource);
    }
  }
}
