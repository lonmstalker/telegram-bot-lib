package io.lonmstalker.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class BotDataSourceFactoryTest {
    private DataSource ds;

    @BeforeEach
    void setup() throws Exception {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        ds = h2;
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate(
                    "CREATE TABLE bot_settings (id INT PRIMARY KEY, token VARCHAR(255), " +
                            "proxy_host VARCHAR(255), proxy_port INT, proxy_type INT, " +
                            "max_threads INT, updates_timeout INT, updates_limit INT, bot_pattern VARCHAR(255))");
        }
    }

    @Test
    void load_data() throws Exception {
        var cipher = new TokenCipherImpl("secretkey123456");
        String enc = cipher.encrypt("TEST_TOKEN");
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("INSERT INTO bot_settings VALUES (1, '" + enc + "', NULL, NULL, 0, 1, 0, 100, '')");
        }

        var data = BotDataSourceFactory.INSTANCE.load(ds, 1, cipher);
        assertEquals("TEST_TOKEN", data.token());
        assertEquals(1, data.config().getMaxThreads());
        assertEquals(0, data.config().getGetUpdatesTimeout());
    }

    @Test
    void bot_not_found_throws() {
        var cipher = new TokenCipherImpl("secretkey123456");
        assertThrows(BotApiException.class,
                () -> BotDataSourceFactory.INSTANCE.load(ds, 99, cipher));
    }

    @Test
    void empty_token_throws() throws Exception {
        var cipher = new TokenCipherImpl("secretkey123456");
        String enc = cipher.encrypt("");
        try (Connection c = ds.getConnection();
             Statement st = c.createStatement()) {
            st.executeUpdate("INSERT INTO bot_settings VALUES (2, '" + enc + "', NULL, NULL, 0, 1, 0, 100, '')");
        }

        assertThrows(BotApiException.class,
                () -> BotDataSourceFactory.INSTANCE.load(ds, 2, cipher));
    }
}
