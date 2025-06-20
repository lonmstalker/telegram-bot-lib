package io.lonmstalker.tgkit.core.user.store;

import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ReadOnlyUserKVStore implements UserKVStore {
  private final @NonNull UserKVStore delegate;

  public ReadOnlyUserKVStore(@NonNull UserKVStore delegate) {
    this.delegate = delegate;
  }

  @Override
  public String get(long uid, @NonNull String k) {
    return delegate.get(uid, k);
  }

  @Override
  public @NonNull Map<String, String> getAll(long uid) {
    return delegate.getAll(uid);
  }

  @Override
  public void put(long uid, @NonNull String k, @NonNull String v) {
    throw new UnsupportedOperationException("read-only");
  }

  @Override
  public void remove(long uid, @NonNull String k) {
    throw new UnsupportedOperationException("read-only");
  }
}
