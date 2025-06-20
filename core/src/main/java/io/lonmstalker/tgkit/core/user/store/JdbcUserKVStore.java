package io.lonmstalker.tgkit.core.user.store;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Key-value хранилище «данные пользователя» с автодетекцией СУБД.
 *
 * <p>Поддержка UPSERT’ов:
 *
 * <ul>
 *   <li>PostgreSQL 9.5+ (`INSERT … ON CONFLICT`)
 *   <li>MySQL 5.7 / MariaDB 10.3 (`ON DUPLICATE KEY UPDATE`)
 *   <li>Oracle 12c+ (`MERGE INTO … USING dual`)
 *   <li>H2 / Derby (`MERGE INTO … VALUES`)
 *   <li>fallback (любая БД) — UPDATE + INSERT, если UPDATE = 0
 * </ul>
 */
public final class JdbcUserKVStore implements UserKVStore {
  private final DataSource ds;
  private final String upsertSql;
  private final Mode mode;

  private enum Mode {
    PG,
    MYSQL,
    ORACLE,
    H2_DERBY,
    GENERIC
  }

  public JdbcUserKVStore(@NonNull DataSource ds) {
    this.ds = ds;
    this.mode = detectMode();
    this.upsertSql = buildUpsertSql(mode);
  }

  @Override
  public String get(long userId, @NonNull String key) {
    String sql = "SELECT v FROM user_kv WHERE user_id=? AND k=?";
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      ps.setString(2, key);
      ResultSet rs = ps.executeQuery();
      return rs.next() ? rs.getString(1) : null;
    } catch (SQLException e) {
      throw new BotApiException(e);
    }
  }

  @Override
  public @NonNull Map<String, String> getAll(long userId) {
    String sql = "SELECT k, v FROM user_kv WHERE user_id=?";
    Map<String, String> map = new HashMap<>();
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) map.put(rs.getString(1), rs.getString(2));
      return map;
    } catch (SQLException e) {
      throw new BotApiException(e);
    }
  }

  @Override
  public void put(long uid, @NonNull String key, @NonNull String val) {
    if (Objects.requireNonNull(mode) == Mode.GENERIC) {
      upsertGeneric(uid, key, val);
    } else {
      upsertNative(uid, key, val);
    }
  }

  @Override
  public void remove(long userId, @NonNull String key) {
    String sql = "DELETE FROM user_kv WHERE user_id=? AND k=?";
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setLong(1, userId);
      ps.setString(2, key);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new BotApiException(e);
    }
  }

  private void upsertNative(long uid, String k, String v) {
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(upsertSql)) {

      ps.setLong(1, uid);
      ps.setString(2, k);
      ps.setString(3, v);
      if (mode == Mode.ORACLE) { // Oracle MERGE: params повторяются
        ps.setLong(4, uid);
        ps.setString(5, k);
        ps.setString(6, v);
      }
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new BotApiException(e);
    }
  }

  /** Универсальный UPDATE → INSERT. Работает на любой JDBC-БД. */
  private void upsertGeneric(long uid, String k, String v) {
    String upd = "UPDATE user_kv SET v=? WHERE user_id=? AND k=?";
    try (Connection c = ds.getConnection();
        PreparedStatement ps = c.prepareStatement(upd)) {
      ps.setString(1, v);
      ps.setLong(2, uid);
      ps.setString(3, k);
      int updated = ps.executeUpdate();
      if (updated == 0) { // не было строки → INSERT
        try (PreparedStatement ins =
            c.prepareStatement("INSERT INTO user_kv(user_id,k,v) VALUES(?,?,?)")) {
          ins.setLong(1, uid);
          ins.setString(2, k);
          ins.setString(3, v);
          ins.executeUpdate();
        }
      }
    } catch (SQLException e) {
      throw new BotApiException(e);
    }
  }

  private @NonNull Mode detectMode() {
    try (Connection c = ds.getConnection()) {
      String db = c.getMetaData().getDatabaseProductName().toLowerCase();
      if (db.contains("postgresql")) return Mode.PG;
      if (db.contains("mysql") || db.contains("mariadb")) return Mode.MYSQL;
      if (db.contains("oracle")) return Mode.ORACLE;
      if (db.contains("h2") || db.contains("derby")) return Mode.H2_DERBY;
    } catch (SQLException ignored) {
    }
    return Mode.GENERIC;
  }

  private static @NonNull String buildUpsertSql(Mode m) {
    return switch (m) {
      case PG ->
          "INSERT INTO user_kv(user_id,k,v) VALUES (?,?,?) "
              + "ON CONFLICT (user_id,k) DO UPDATE SET v = EXCLUDED.v";
      case MYSQL ->
          "INSERT INTO user_kv(user_id,k,v) VALUES (?,?,?) "
              + "ON DUPLICATE KEY UPDATE v = VALUES(v)";
      case ORACLE ->
          """
                    MERGE INTO user_kv t
                    USING (SELECT ? user_id, ? k, ? v FROM dual) s
                      ON (t.user_id = s.user_id AND t.k = s.k)
                    WHEN MATCHED THEN UPDATE SET t.v = s.v
                    WHEN NOT MATCHED THEN
                      INSERT (user_id,k,v) VALUES(s.user_id,s.k,s.v)
                    """;
      case H2_DERBY -> "MERGE INTO user_kv(user_id,k,v) VALUES (?,?,?)";
      default -> ""; // GENERIC (UPDATE+INSERT)
    };
  }
}
