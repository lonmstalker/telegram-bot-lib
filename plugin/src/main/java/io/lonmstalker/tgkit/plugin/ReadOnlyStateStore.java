package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.core.state.StateStore;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Обёртка над StateStore, запрещающая запись.
 */
public class ReadOnlyStateStore implements StateStore {
    private final StateStore delegate;

    public ReadOnlyStateStore(StateStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public @Nullable String get(@NonNull String chatId) {
        return delegate.get(chatId);
    }

    @Override
    public void set(@NonNull String chatId, @NonNull String value) {
        throw new SecurityException("StateStore is read-only");
    }
}
