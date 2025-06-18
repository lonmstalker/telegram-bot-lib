package io.lonmstalker.tgkit.app;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.plugin.BotPluginManager;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        BotCoreInitializer.init();
        BotSecurityInitializer.init();

        // 1) базовая папка проекта (где запущена JVM)
        Path projectRoot = Paths.get(System.getProperty("user.dir")).toAbsolutePath();

        // 2) папка с собранными плагинами
        Path pluginsDir = args.length > 0 ? Path.of(args[0]) : projectRoot
                .resolve("examples")
                .resolve("plugin-demo")
                .resolve("plugin-example")
                .resolve("target");

        try (var mgr = new BotPluginManager()) {
            mgr.loadAll(pluginsDir);
        }
    }
}
