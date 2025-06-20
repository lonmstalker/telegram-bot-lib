package io.lonmstalker.tgkit.plugin;

public interface BotPlugin extends PluginLifecycle {

  default void start() throws Exception {}

  default void stop() throws Exception {}
}
