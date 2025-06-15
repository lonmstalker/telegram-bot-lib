package io.lonmstalker.tgkit.plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Наивная реализация {@link EventBus} без потокобезопасности.
 */
public class DefaultEventBus implements EventBus {
    private final List<MessageHandler> handlers = new ArrayList<>();

    @Override
    public void subscribe(MessageHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void publish(String message) {
        for (MessageHandler handler : handlers) {
            handler.onMessage(message);
        }
    }
}
