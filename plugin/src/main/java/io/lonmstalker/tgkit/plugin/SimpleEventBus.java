package io.lonmstalker.tgkit.plugin;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Простая реализация шины событий без внешних зависимостей.
 */
public final class SimpleEventBus implements EventBus {

    private final ConcurrentHashMap<Class<?>, CopyOnWriteArrayList<EventHandler<?>>> handlers = new ConcurrentHashMap<>();

    @Override
    public <T> Subscription subscribe(Class<T> type, EventHandler<T> handler) {
        handlers.computeIfAbsent(type, k -> new CopyOnWriteArrayList<>()).add(handler);
        return () -> handlers.getOrDefault(type, new CopyOnWriteArrayList<>()).remove(handler);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void publish(Object event) {
        Class<?> cls = event.getClass();
        List<EventHandler<?>> list = handlers.get(cls);
        if (list == null) return;
        for (EventHandler<?> h : list) {
            ((EventHandler<Object>) h).handle(event);
        }
    }
}
