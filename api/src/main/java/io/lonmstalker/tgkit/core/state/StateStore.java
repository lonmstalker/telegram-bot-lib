package io.lonmstalker.tgkit.core.state;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface StateStore {

    @Nullable
    String get(@NonNull String chatId);

    void set(@NonNull String chatId, @NonNull String value);
}
