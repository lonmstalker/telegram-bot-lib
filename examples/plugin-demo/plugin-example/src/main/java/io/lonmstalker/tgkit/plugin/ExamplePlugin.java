package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePlugin implements BotPlugin {

    private static final Logger log = LoggerFactory.getLogger(ExamplePlugin.class);

    @Override
    public void onLoad(@NonNull BotPluginContext ctx) {
        log.info("onLoad");
    }

    @Override
    public void start() {
        log.info("start");
    }

    @Override
    public void beforeStop() {
        log.info("beforeStop");
    }

    @Override
    public void stop() {
        log.info("stop");
    }

    @Override
    public void afterStop() {
        log.info("afterStop");
    }

    @Override
    public void onUnload() {
        log.info("onUnload");
    }
}
