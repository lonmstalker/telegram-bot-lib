package io.craftbot.security.spi;

public interface StateStore {
    String get(long chat, String key);
    void set(long chat, String key, String value, int ttlSeconds);
}
