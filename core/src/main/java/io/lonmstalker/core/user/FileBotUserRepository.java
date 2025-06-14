package io.lonmstalker.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

import java.sql.*;
import java.util.Collections;
import java.util.Set;

/**
 * File-based implementation of {@link BotUserRepository} using H2 database.
 */
public class FileBotUserRepository implements BotUserRepository {
    private final Connection connection;

    public FileBotUserRepository(@NonNull String dbFile) {
        try {
            this.connection = DriverManager.getConnection("jdbc:h2:file:" + dbFile);
            try (Statement st = connection.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS bot_user(" +
                        "id BIGINT PRIMARY KEY, chat_id VARCHAR(20), roles VARCHAR)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("argument")
    public @NonNull BotUserInfo getOrCreate(@NonNull User telegramUser) {
        long id = telegramUser.getId();
        try {
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT chat_id, roles FROM bot_user WHERE id=?")) {
                ps.setLong(1, id);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String chatId = rs.getString(1);
                    return new DbUserInfo(chatId, Collections.emptySet());
                }
            }

            String chatId = String.valueOf(id);
            try (PreparedStatement ins = connection.prepareStatement(
                    "INSERT INTO bot_user(id, chat_id, roles) VALUES(?,?,?)")) {
                ins.setLong(1, id);
                ins.setString(2, chatId);
                ins.setString(3, "");
                ins.executeUpdate();
            }
            return new DbUserInfo(chatId, Collections.emptySet());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private record DbUserInfo(@NonNull String chatId,
                              @NonNull Set<String> roles) implements BotUserInfo {}
}
