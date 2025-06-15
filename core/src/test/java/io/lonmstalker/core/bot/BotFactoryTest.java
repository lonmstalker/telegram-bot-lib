package io.lonmstalker.core.bot;

import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class BotFactoryTest {

    @Test
    void createFromToken() {
        BotAdapter adapter = u -> null;
        BotConfig cfg = BotConfig.builder().build();
        Bot bot = BotFactory.INSTANCE.from("TOKEN", cfg, adapter);
        assertNotNull(bot);
        assertEquals("TOKEN", bot.token());
    }

    @Test
    void createFromWebhook() {
        BotAdapter adapter = u -> null;
        BotConfig cfg = BotConfig.builder().build();
        SetWebhook hook = new SetWebhook();
        Bot bot = BotFactory.INSTANCE.from("TOKEN", cfg, adapter, hook);
        assertNotNull(bot);
        assertEquals("TOKEN", bot.token());
    }

    @Test
    void createFromDataSource() throws Exception {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:bot;DB_CLOSE_DELAY=-1");
        DataSource ds = h2;
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("CREATE TABLE bot_settings (id INT PRIMARY KEY, token VARCHAR(255), proxy_host VARCHAR(255), proxy_port INT, proxy_type INT, max_threads INT, updates_timeout INT, updates_limit INT, bot_pattern VARCHAR(255))");
        }
        TokenCipherImpl cipher = new TokenCipherImpl("secretkey123456");
        String enc = cipher.encrypt("TOKEN");
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.executeUpdate("INSERT INTO bot_settings VALUES (1, '" + enc + "', NULL, NULL, 0, 1, 0, 100, '')");
        }
        BotDataSourceConfig cfg = BotDataSourceConfig.builder().dataSource(ds).build();
        BotAdapter adapter = u -> null;
        Bot bot = BotFactory.INSTANCE.from(1, cfg, adapter, cipher);
        assertNotNull(bot);
        assertEquals("TOKEN", bot.token());
    }
}
