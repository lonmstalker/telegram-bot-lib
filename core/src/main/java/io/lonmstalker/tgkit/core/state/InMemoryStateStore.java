package io.lonmstalker.tgkit.core.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class InMemoryStateStore implements StateStore {
  private final Map<String, String> store = new ConcurrentHashMap<>();

  @Override
  public @Nullable String get(@NonNull String chatId) {
    return store.get(chatId);
  }

  @Override
  public void set(@NonNull String chatId, @NonNull String value) {
    store.put(chatId, value);
  }
}
