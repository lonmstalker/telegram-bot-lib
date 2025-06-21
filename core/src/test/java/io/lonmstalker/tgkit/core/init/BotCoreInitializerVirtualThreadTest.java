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
package io.github.tgkit.core.init;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.webhook.WebhookServer;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/**
 * Проверяет, что {@link BotCoreInitializer} корректно создаёт виртуальные пулы потоков и завершает
 * их работу.
 */
class BotCoreInitializerVirtualThreadTest {

  @Test
  void init_and_shutdown_with_virtual_executors() throws Exception {
    Field started = BotCoreInitializer.class.getDeclaredField("started");
    started.setAccessible(true);
    started.setBoolean(null, false);

    BotGlobalConfig.INSTANCE.executors().cpuPoolSize(1).scheduledPoolSize(1);
    BotCoreInitializer.init();

    ExecutorService cpu = BotGlobalConfig.INSTANCE.executors().getCpuExecutorService();
    ScheduledExecutorService sched =
        BotGlobalConfig.INSTANCE.executors().getScheduledExecutorService();

    Future<Boolean> cpuResult = cpu.submit(() -> Thread.currentThread().isVirtual());
    Future<Boolean> schedResult =
        sched.schedule(() -> Thread.currentThread().isVirtual(), 0, TimeUnit.MILLISECONDS);

    assertThat(cpuResult.get(1, TimeUnit.SECONDS)).isTrue();
    assertThat(schedResult.get(1, TimeUnit.SECONDS)).isTrue();

    BotGlobalConfig.INSTANCE.executors().close();
    WebhookServer server = BotGlobalConfig.INSTANCE.webhook().server();
    if (server != null) {
      server.close();
    }

    assertThat(cpu.isShutdown()).isTrue();
    assertThat(sched.isShutdown()).isTrue();
  }
}
