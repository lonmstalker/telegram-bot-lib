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
package io.github.tgkit.testkit;

import io.github.tgkit.internal.init.BotCoreInitializer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Утилита для подготовки тестов. Гарантирует инициализацию {@link BotCoreInitializer} только один
 * раз за JVM.
 */
public final class TestBotBootstrap {

  private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

  private TestBotBootstrap() {}

  /**
   * Инициализирует ядро TgKit один раз.
   *
   * <pre>{@code
   * TestBotBootstrap.initOnce();
   * }</pre>
   */
  public static void initOnce() {
    if (INITIALIZED.compareAndSet(false, true)) {
      BotCoreInitializer.init();
    }
  }
}
