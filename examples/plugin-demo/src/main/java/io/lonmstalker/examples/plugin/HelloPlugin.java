package io.lonmstalker.examples.plugin;

import io.lonmstalker.tgkit.plugin.spi.Plugin;
import io.lonmstalker.tgkit.plugin.spi.PluginContext;

/**
 * Простейший плагин, печатающий приветствие в консоль при старте.
 */
public class HelloPlugin implements Plugin {

    @Override
    public int abiVersion() {
        return 1;
    }

    @Override
    public void init(PluginContext ctx) {
        // ничего не делаем
    }

    @Override
    public void start() {
        System.out.println("Hello from plugin!");
    }

    @Override
    public void stop() {
        System.out.println("Goodbye from plugin!");
    }
}
