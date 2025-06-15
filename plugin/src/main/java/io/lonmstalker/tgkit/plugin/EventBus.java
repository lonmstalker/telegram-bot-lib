package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface EventBus {
    <T> @NonNull Subscription subscribe(@NonNull Class<T> type, @NonNull EventHandler<T> handler);
    void publish(@NonNull Object event);
}
