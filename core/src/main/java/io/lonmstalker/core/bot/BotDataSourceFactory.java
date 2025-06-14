package io.lonmstalker.core.bot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import io.lonmstalker.core.exception.BotApiException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Extracts bot information from database.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotDataSourceFactory {
    public static final BotDataSourceFactory INSTANCE = new BotDataSourceFactory();

    private static final String SELECT_QUERY =
            "SELECT token, proxy, max_threads, get_updates_timeout, get_updates_limit FROM bots WHERE id = ?";

    private static final String DEFAULT_PROXY = "";
    private static final int DEFAULT_MAX_THREADS = 1;
    private static final int DEFAULT_UPDATES_TIMEOUT = 0;
    private static final int DEFAULT_UPDATES_LIMIT = 100;

    private static String stringOrDefault(@NonNull ResultSet rs,
                                          @NonNull String column,
                                          @NonNull String def) throws SQLException {
        var value = rs.getString(column);
        if (value == null) {
            log.warn("{} is null, using default: {}", column, def);
            value = def;
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

    public @NonNull BotData load(@NonNull DataSource dataSource, long id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_QUERY)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new BotApiException("Bot not found");
                }

                String token = stringOrDefault(rs, "token", "");
                String proxy = stringOrDefault(rs, "proxy", DEFAULT_PROXY);
                int maxThreads = intOrDefault(rs, "max_threads", DEFAULT_MAX_THREADS);
                int timeout = intOrDefault(rs, "get_updates_timeout", DEFAULT_UPDATES_TIMEOUT);
                int limit = intOrDefault(rs, "get_updates_limit", DEFAULT_UPDATES_LIMIT);

                BotConfig config = new BotConfig();
                config.setProxyHost(proxy);
                config.setMaxThreads(maxThreads);
                config.setGetUpdatesTimeout(timeout);
                config.setGetUpdatesLimit(limit);
                return new BotData(token, config);
            }
        } catch (SQLException ex) {
            throw new BotApiException("Error loading bot data", ex);
        }
    }

    public record BotData(@NonNull String token, @NonNull BotConfig config) {
    }
}
