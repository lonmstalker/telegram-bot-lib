CREATE TABLE IF NOT EXISTS bot_settings (
    id SERIAL PRIMARY KEY,
    token VARCHAR(512) NOT NULL,
    proxy_host VARCHAR(255),
    proxy_port INTEGER,
    proxy_type SMALLINT,
    updates_timeout INTEGER,
    updates_limit INTEGER,
    max_threads INTEGER
);

COMMENT ON TABLE bot_settings IS 'Параметры работы бота и прокси';
COMMENT ON COLUMN bot_settings.token IS 'Зашифрованный токен Telegram-бота';
COMMENT ON COLUMN bot_settings.proxy_type IS 'Тип прокси: 1 - HTTP, 2 - SOCKS4, 3 - SOCKS5';
COMMENT ON COLUMN bot_settings.updates_timeout IS 'Таймаут ожидания обновлений, сек.';
COMMENT ON COLUMN bot_settings.updates_limit IS 'Максимум обновлений за запрос';
COMMENT ON COLUMN bot_settings.max_threads IS 'Максимальное число потоков';
