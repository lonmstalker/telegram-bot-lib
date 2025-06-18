package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

public class ExamplePlugin implements BotPlugin {

    @Override
    public void onLoad(@NonNull BotPluginContext ctx) {
        System.out.println("[Example] onLoad");
    }

    @Override
    public void start() {
        System.out.println("[Example] start");
    }

    @Override
    public void beforeStop() {
        System.out.println("[Example] beforeStop");
    }

    @Override
    public void stop() {
        System.out.println("[Example] stop");
    }

    @Override
    public void afterStop() {
        System.out.println("[Example] afterStop");
    }

    @Override
    public void onUnload() {
        System.out.println("[Example] onUnload");
    }
}
