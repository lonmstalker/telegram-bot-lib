package io.lonmstalker.core.bot;

import io.lonmstalker.core.utils.TokenCipherImpl;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotFactory")
class BotFactoryTest {
    DataSource ds;

    @BeforeEach
    void init() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:bot;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE bot_settings (id INT PRIMARY KEY, token VARCHAR(512), proxy_host VARCHAR(255), proxy_port INT, proxy_type SMALLINT, updates_timeout INT, updates_limit INT, max_threads INT)");
            String token = new TokenCipherImpl("1234567890123456").encrypt("tok");
            st.executeUpdate("INSERT INTO bot_settings (id, token) VALUES (5, '" + token + "')");
        }
    }

    @AfterEach
    void close() {
        ((JdbcConnectionPool) ds).dispose();
    }

    @Test
    @DisplayName("создаёт бота по токену")
    void createFromToken() {
        Bot bot = BotFactory.INSTANCE.from("tok", new BotConfig(), update -> null);
        assertNotNull(bot);
        assertEquals("tok", bot.token());
    }

    @Test
    @DisplayName("создаёт бота с вебхуком")
    void createWithWebhook() {
        Bot bot = BotFactory.INSTANCE.from("tok", new BotConfig(), update -> null, new SetWebhook());
        assertNotNull(bot);
    }

    @Test
    @DisplayName("создаёт бота из DataSource")
    void createFromDataSource() {
        BotDataSourceConfig cfg = BotDataSourceConfig.builder().dataSource(ds).build();
        Bot bot = BotFactory.INSTANCE.from(5, cfg, update -> null, new TokenCipherImpl("1234567890123456"));
        assertEquals("tok", bot.token());
    }
}
