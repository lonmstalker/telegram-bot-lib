package io.lonmstalker.tgkit.plugin.api;

public interface Plugin {
    default void init(PluginContext ctx)      throws Exception {}
    default void start()                      throws Exception {}
    default void stop()                       throws Exception {}
}