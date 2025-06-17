package io.lonmstalker.tgkit.core.user.store;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;

@RequiredArgsConstructor
public final class ReadOnlyUserKVStore implements UserKVStore {
    private final @NonNull UserKVStore delegate;

    @Override
    public String get(long uid, String k) {
        return delegate.get(uid, k);
    }

    @Override
    public @NonNull Map<String, String> getAll(long uid) {
        return delegate.getAll(uid);
    }

    @Override
    public void put(long uid, String k, String v) {
        throw new UnsupportedOperationException("read-only");
    }

    @Override
    public void remove(long uid, String k) {
        throw new UnsupportedOperationException("read-only");
    }
}

