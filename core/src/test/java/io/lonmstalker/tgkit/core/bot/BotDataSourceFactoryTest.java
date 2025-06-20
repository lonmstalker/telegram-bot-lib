/*
 * Copyright (C) 2024 the original author or authors.
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

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.crypto.TokenCipherImpl;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.sql.Connection;
import java.sql.Statement;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BotDataSourceFactoryTest {
  private DataSource ds;

  static {
    BotCoreInitializer.init();
  }

  @BeforeEach
  void setup() throws Exception {
    JdbcDataSource h2 = new JdbcDataSource();
    h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
    ds = h2;
    try (Connection c = ds.getConnection();
        Statement st = c.createStatement()) {
      st.executeUpdate(
          "CREATE TABLE IF NOT EXISTS bot_settings (id INT PRIMARY KEY, token VARCHAR(255), "
              + "proxy_host VARCHAR(255), proxy_port INT, proxy_type INT, "
              + "max_threads INT, updates_timeout INT, updates_limit INT, bot_group VARCHAR(255))");
    }
  }

  @Test
  void load_data() throws Exception {
    var cipher = new TokenCipherImpl("secretkey123456");
    String enc = cipher.encrypt("TEST_TOKEN");
    try (Connection c = ds.getConnection();
        Statement st = c.createStatement()) {
      st.executeUpdate(
          "INSERT INTO bot_settings VALUES (1, '" + enc + "', NULL, NULL, 0, 1, 0, 100, '')");
    }

    var data = BotDataSourceFactory.INSTANCE.load(ds, 1, cipher);
    assertEquals("TEST_TOKEN", data.token());
    assertEquals(1, data.config().getMaxThreads());
    assertEquals(0, data.config().getGetUpdatesTimeout());
  }

  @Test
  void bot_not_found_throws() {
    var cipher = new TokenCipherImpl("secretkey123456");
    assertThrows(BotApiException.class, () -> BotDataSourceFactory.INSTANCE.load(ds, 99, cipher));
  }

  @Test
  void empty_token_throws() throws Exception {
    var cipher = new TokenCipherImpl("secretkey123456");
    String enc = cipher.encrypt("");
    try (Connection c = ds.getConnection();
        Statement st = c.createStatement()) {
      st.executeUpdate(
          "INSERT INTO bot_settings VALUES (2, '" + enc + "', NULL, NULL, 0, 1, 0, 100, '')");
    }

    assertThrows(BotApiException.class, () -> BotDataSourceFactory.INSTANCE.load(ds, 2, cipher));
  }
}
