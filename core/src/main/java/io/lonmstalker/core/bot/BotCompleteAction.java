package io.lonmstalker.core.bot;

@FunctionalInterface
public interface BotCompleteAction {
    void complete() throws Exception;
}