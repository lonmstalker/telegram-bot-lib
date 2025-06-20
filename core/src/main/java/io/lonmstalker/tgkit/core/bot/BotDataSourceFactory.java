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
package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.crypto.TokenCipher;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/** Extracts bot information from database. */
public final class BotDataSourceFactory {
  private static final Logger log = LoggerFactory.getLogger(BotDataSourceFactory.class);

  private BotDataSourceFactory() {}

  public static final BotDataSourceFactory INSTANCE = new BotDataSourceFactory();

  // language=SQL
  private static final String SELECT_QUERY =
      "SELECT token, proxy_host, proxy_port, proxy_type, max_threads, updates_timeout, updates_limit, bot_group "
          + "FROM bot_settings WHERE id = ?";

  private static final int DEFAULT_MAX_THREADS = 1;
  private static final int DEFAULT_UPDATES_TIMEOUT = 0;
  private static final int DEFAULT_UPDATES_LIMIT = 100;

  private static @Nullable String stringOrDefault(
      @NonNull ResultSet rs, @NonNull String column, @Nullable String def) throws SQLException {
    var value = rs.getString(column);
    if (value == null) {
      if (def != null) {
        value = def;
        log.warn("{} is null, using default: {}", column, def);
      }
    }
    return value;
  }

  private static int intOrDefault(@NonNull ResultSet rs, @NonNull String column, int def)
      throws SQLException {
    var value = rs.getInt(column);
    if (rs.wasNull()) {
      log.warn("{} is null, using default: {}", column, def);
      value = def;
    }
    return value;
  }

  /**
   * Читает параметры бота из DataSource.
   *
   * @param dataSource источник данных
   * @param id идентификатор в БД
   * @param cipher дешифратор токена
   * @return параметры бота
   */
  @SuppressWarnings("argument")
  public @NonNull BotData load(
      @NonNull DataSource dataSource, long id, @NonNull TokenCipher cipher) {
    try (Connection connection = dataSource.getConnection();
        PreparedStatement ps = connection.prepareStatement(SELECT_QUERY)) {
      ps.setLong(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          throw new BotApiException("Bot not found");
        }

        String token = cipher.decrypt(Objects.requireNonNull(stringOrDefault(rs, "token", "")));
        String proxyHost = stringOrDefault(rs, "proxy_host", null);
        int proxyPort = intOrDefault(rs, "proxy_port", 0);
        int proxyTypeValue =
            intOrDefault(rs, "proxy_type", DefaultBotOptions.ProxyType.NO_PROXY.ordinal());
        int maxThreads = intOrDefault(rs, "max_threads", DEFAULT_MAX_THREADS);
        int timeout = intOrDefault(rs, "updates_timeout", DEFAULT_UPDATES_TIMEOUT);
        int limit = intOrDefault(rs, "updates_limit", DEFAULT_UPDATES_LIMIT);
        String botGroup = stringOrDefault(rs, "bot_group", "");

        if (StringUtils.isBlank(token)) {
          throw new BotApiException("Bot token is empty");
        }

        BotConfig config =
            BotConfig.builder()
                .proxyHost(proxyHost)
                .proxyPort(proxyPort)
                .proxyType(DefaultBotOptions.ProxyType.values()[proxyTypeValue])
                .maxThreads(maxThreads)
                .getUpdatesTimeout(timeout)
                .getUpdatesLimit(limit)
                .botGroup(Objects.requireNonNull(botGroup))
                .build();

        return new BotData(token, config);
      }
    } catch (SQLException ex) {
      throw new BotApiException("Error loading bot data", ex);
    }
  }

  public record BotData(@NonNull String token, @NonNull BotConfig config) {}
}
