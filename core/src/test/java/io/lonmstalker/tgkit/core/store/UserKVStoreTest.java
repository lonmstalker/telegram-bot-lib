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
package io.lonmstalker.tgkit.core.store;

import io.lonmstalker.tgkit.core.user.store.InMemoryUserKVStore;
import io.lonmstalker.tgkit.core.user.store.JdbcUserKVStore;
import io.lonmstalker.tgkit.core.user.store.ReadOnlyUserKVStore;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.util.Map;
import javax.sql.DataSource;
import org.assertj.core.api.WithAssertions;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

@SuppressWarnings("ConstantConditions")
class UserKVStoreTest implements WithAssertions {

  static {
    TestBotBootstrap.initOnce();
  }

  /* ---------- shared test data ---------- */
  private static final long UID = 77L;
  private static final String KEY = "lang";

  /* ===================================================================
   *  1. In-Memory store
   * ================================================================= */
  @Nested
  class InMemoryStore {
    InMemoryUserKVStore store;

    @BeforeEach
    void init() {
      store = new InMemoryUserKVStore();
    }

    @Test
    void putGetRemove() {
      store.put(UID, KEY, "ru");
      assertThat(store.get(UID, KEY)).isEqualTo("ru");

      store.remove(UID, KEY);
      assertThat(store.get(UID, KEY)).isNull();
    }

    @Test
    void getAllReturnsCopy() {
      store.put(UID, KEY, "en");
      Map<String, String> all = store.getAll(UID);

      assertThat(all).containsEntry(KEY, "en");
      // modify copy – оригинал не меняется
      try {
        all.clear();
      } catch (Exception ignored) {
      }
      ;
      assertThat(store.getAll(UID)).isNotEmpty();
    }
  }

  /* ===================================================================
   *  2. JDBC store (H2 in-memory)
   * ================================================================= */
  @Nested
  class JdbcStore {

    static DataSource ds;
    JdbcUserKVStore store;

    @BeforeAll
    static void startDb() throws Exception {
      JdbcDataSource h2 = new JdbcDataSource();
      h2.setURL("jdbc:h2:mem:kv;DB_CLOSE_DELAY=-1");
      ds = h2;

      try (var c = ds.getConnection();
          var st = c.createStatement()) {
        st.execute(
            """
                            create table user_kv(
                              user_id bigint not null,
                              k varchar(64) not null,
                              v varchar(512),
                              primary key (user_id, k)
                            );
                        """);
      }
    }

    @BeforeEach
    void init() {
      store = new JdbcUserKVStore(ds);
    }

    @AfterEach
    void cleanup() throws Exception {
      try (var c = ds.getConnection();
          var st = c.createStatement()) {
        st.execute("delete from user_kv");
      }
    }

    @Test
    void upsertWorks() {
      store.put(UID, KEY, "ru");
      assertThat(store.get(UID, KEY)).isEqualTo("ru");

      // second put should update
      store.put(UID, KEY, "en");
      assertThat(store.get(UID, KEY)).isEqualTo("en");
    }

    @Test
    void getAllAndRemove() {
      store.put(UID, KEY, "ru");
      store.put(UID, "theme", "dark");

      assertThat(store.getAll(UID)).containsEntry(KEY, "ru").containsEntry("theme", "dark");

      store.remove(UID, KEY);
      assertThat(store.get(UID, KEY)).isNull();
    }
  }

  /* ===================================================================
   *  3. Read-Only proxy
   * ================================================================= */
  @Nested
  class ReadOnlyStore {

    ReadOnlyUserKVStore ro;
    InMemoryUserKVStore backing;

    @BeforeEach
    void init() {
      backing = new InMemoryUserKVStore();
      backing.put(UID, KEY, "ru");
      ro = new ReadOnlyUserKVStore(backing);
    }

    @Test
    void readDelegates() {
      assertThat(ro.get(UID, KEY)).isEqualTo("ru");
      assertThat(ro.getAll(UID)).containsEntry(KEY, "ru");
    }

    @Test
    void putThrows() {
      assertThatThrownBy(() -> ro.put(UID, KEY, "en"))
          .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void removeThrows() {
      assertThatThrownBy(() -> ro.remove(UID, KEY))
          .isInstanceOf(UnsupportedOperationException.class);
    }
  }
}
