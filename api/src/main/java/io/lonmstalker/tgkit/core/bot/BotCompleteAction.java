package io.lonmstalker.tgkit.core.bot;

@FunctionalInterface
public interface BotCompleteAction {
  void complete() throws Exception;
}
