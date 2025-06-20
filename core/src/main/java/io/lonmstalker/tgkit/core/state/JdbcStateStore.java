package io.lonmstalker.tgkit.core.state;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class JdbcStateStore implements StateStore {
  private static final String CREATE_TABLE =
      "CREATE TABLE IF NOT EXISTS bot_state(chat_id VARCHAR PRIMARY KEY, value VARCHAR)";
  private static final String SELECT_QUERY = "SELECT value FROM bot_state WHERE chat_id=?";
  private static final String UPDATE_QUERY = "UPDATE bot_state SET value=? WHERE chat_id=?";
  private static final String INSERT_QUERY = "INSERT INTO bot_state(chat_id, value) VALUES(?, ?)";

  private final DataSource dataSource;

  public JdbcStateStore(@NonNull DataSource dataSource) {
    this.dataSource = dataSource;
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(CREATE_TABLE)) {
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public @Nullable String get(@NonNull String chatId) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement ps = conn.prepareStatement(SELECT_QUERY)) {
      ps.setString(1, chatId);
      try (ResultSet rs = ps.executeQuery()) {
        return rs.next() ? rs.getString(1) : null;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void set(@NonNull String chatId, @NonNull String value) {
    try (Connection conn = dataSource.getConnection();
        PreparedStatement upd = conn.prepareStatement(UPDATE_QUERY)) {
      upd.setString(1, value);
      upd.setString(2, chatId);
      int updated = upd.executeUpdate();
      if (updated == 0) {
        try (PreparedStatement ins = conn.prepareStatement(INSERT_QUERY)) {
          ins.setString(1, chatId);
          ins.setString(2, value);
          ins.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
