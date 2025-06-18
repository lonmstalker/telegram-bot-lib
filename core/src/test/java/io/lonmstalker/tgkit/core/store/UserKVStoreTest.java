
package io.lonmstalker.tgkit.core.store;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.user.store.InMemoryUserKVStore;
import io.lonmstalker.tgkit.core.user.store.JdbcUserKVStore;
import io.lonmstalker.tgkit.core.user.store.ReadOnlyUserKVStore;
import org.assertj.core.api.WithAssertions;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.*;

import javax.sql.DataSource;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
class UserKVStoreTest implements WithAssertions {

    static {
        BotCoreInitializer.init();
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
            } catch (Exception ignored) {};
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
                st.execute("""
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

            assertThat(store.getAll(UID))
                    .containsEntry(KEY, "ru")
                    .containsEntry("theme", "dark");

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
