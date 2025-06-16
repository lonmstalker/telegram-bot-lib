package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Наивная реализация {@link EventBus} для однопоточного использования.
 * <p>
 * Не потокобезопасна. Для многопоточной среды используйте {@link SimpleEventBus}.
 * Все вызовы должны происходить из того потока, где был создан экземпляр.
 */
public class DefaultEventBus implements EventBus {
    private final List<MessageHandler> handlers = new ArrayList<>();
    private final Thread owner = Thread.currentThread();

    private void checkThread() {
        if (!Thread.currentThread().equals(owner)) {
            throw new IllegalStateException(
                "DefaultEventBus is single-thread only; use SimpleEventBus instead");
        }
    }

    @Override
    public void subscribe(@NonNull MessageHandler handler) {
        checkThread();
        handlers.add(handler);
    }

    @Override
    public void publish(@NonNull String message) {
        checkThread();
        for (MessageHandler handler : handlers) {
            handler.onMessage(message);
        }
    }
}
