package io.lonmstalker.tgkit.plugin;

@FunctionalInterface
public interface EventHandler<T> {
    void handle(T event);
}
