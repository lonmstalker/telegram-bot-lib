/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    Path pluginsDir =
        args.length > 0
            ? Path.of(args[0])
            : projectRoot
                .resolve("examples")
                .resolve("plugin-demo")
                .resolve("plugin-example")
                .resolve("target");

    try (var mgr = new BotPluginManager()) {
      mgr.loadAll(pluginsDir);
    }
  }
}
