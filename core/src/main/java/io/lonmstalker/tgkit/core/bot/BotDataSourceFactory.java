package io.lonmstalker.tgkit.core.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.crypto.TokenCipher;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Extracts bot information from database.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotDataSourceFactory {
    public static final BotDataSourceFactory INSTANCE = new BotDataSourceFactory();

    // language=SQL
    private static final String SELECT_QUERY =
            "SELECT token, proxy_host, proxy_port, proxy_type, max_threads, updates_timeout, updates_limit, bot_group " +
                    "FROM bot_settings WHERE id = ?";

    private static final int DEFAULT_MAX_THREADS = 1;
    private static final int DEFAULT_UPDATES_TIMEOUT = 0;
    private static final int DEFAULT_UPDATES_LIMIT = 100;

    private static @Nullable String stringOrDefault(@NonNull ResultSet rs,
                                                    @NonNull String column,
                                                    @Nullable String def) throws SQLException {
        var value = rs.getString(column);
        if (value == null) {
            if (def != null) {
                value = def;
                log.warn("{} is null, using default: {}", column, def);
            }
        }
        return value;
    }

    private static int intOrDefault(@NonNull ResultSet rs,
                                    @NonNull String column,
                                    int def) throws SQLException {
        var value = rs.getInt(column);
        if (rs.wasNull()) {
            log.warn("{} is null, using default: {}", column, def);
            value = def;
        }
        return value;
    }

    @SuppressWarnings("argument")
    public @NonNull BotData load(@NonNull DataSource dataSource,
                                 long id,
                                 @NonNull TokenCipher cipher) {
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
                int proxyTypeValue = intOrDefault(rs, "proxy_type", DefaultBotOptions.ProxyType.NO_PROXY.ordinal());
                int maxThreads = intOrDefault(rs, "max_threads", DEFAULT_MAX_THREADS);
                int timeout = intOrDefault(rs, "updates_timeout", DEFAULT_UPDATES_TIMEOUT);
                int limit = intOrDefault(rs, "updates_limit", DEFAULT_UPDATES_LIMIT);
                String botGroup = stringOrDefault(rs, "bot_group", "");

                if (StringUtils.isBlank(token)) {
                    throw new BotApiException("Bot token is empty");
                }

                BotConfig config = BotConfig.builder()
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

    public record BotData(@NonNull String token, @NonNull BotConfig config) {
    }
}
