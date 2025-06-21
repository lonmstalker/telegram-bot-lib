/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.internal.bot;

import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Настройки подключения и необязательный {@link BotConfig} для работы с данными конкретного бота.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * BotDataSourceConfig cfg = BotDataSourceConfig.builder()
 *     .dataSource(ds)
 *     .botConfig(botCfg)
 *     .build();
 * }</pre>
 */
public class BotDataSourceConfig {
  private @Nullable BotConfig botConfig;
  private @NonNull DataSource dataSource;

  private BotDataSourceConfig(@Nullable BotConfig botConfig, @NonNull DataSource dataSource) {
    this.botConfig = botConfig;
    this.dataSource = dataSource;
  }

  public static Builder builder() {
    return new Builder();
  }

  public @Nullable BotConfig getBotConfig() {
    return botConfig;
  }

  public @NonNull DataSource getDataSource() {
    return dataSource;
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
