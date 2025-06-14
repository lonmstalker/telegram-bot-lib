import io.lonmstalker.core.ProxyType;
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.BotDataSourceFactory;
import io.lonmstalker.core.utils.TokenCipher;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotDataSourceFactory")
class BotDataSourceFactoryTest {
    DataSource ds;
    TokenCipher cipher = new TokenCipherImpl("1234567890123456");

    @BeforeEach
    void init() throws Exception {
        ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "sa", "");
        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("CREATE TABLE bot_settings (id INT PRIMARY KEY, token VARCHAR(512), proxy_host VARCHAR(255), proxy_port INT, proxy_type SMALLINT, updates_timeout INT, updates_limit INT, max_threads INT)");
            String enc = cipher.encrypt("token");
            st.executeUpdate("INSERT INTO bot_settings (id, token) VALUES (1, '" + enc + "')");
        }
    }

    @AfterEach
    void close() throws Exception {
        ((JdbcConnectionPool) ds).dispose();
    }

    @Test
    @DisplayName("загружает конфигурацию из базы")
    void loadData() {
        BotDataSourceFactory.BotData data = BotDataSourceFactory.INSTANCE.load(ds,1,cipher);
        assertEquals("token", data.token());
        BotConfig cfg = data.config();
        assertEquals(ProxyType.HTTP, cfg.getProxyType());
        assertEquals(1, cfg.getMaxThreads());
        assertEquals(0, cfg.getGetUpdatesTimeout());
        assertEquals(100, cfg.getGetUpdatesLimit());
    }
}
