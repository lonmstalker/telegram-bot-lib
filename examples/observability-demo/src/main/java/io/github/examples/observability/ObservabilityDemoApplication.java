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

package io.github.examples.observability;

import io.github.tgkit.observability.BotObservability;
import io.github.tgkit.observability.ObservabilityInterceptor;
import io.github.tgkit.core.BotAdapter;
import io.github.tgkit.core.bot.Bot;
import io.github.tgkit.core.bot.BotAdapterImpl;
import io.github.tgkit.core.bot.BotConfig;
import io.github.tgkit.core.bot.BotFactory;
import io.github.tgkit.core.init.BotCoreInitializer;

public class ObservabilityDemoApplication {

  public static void main(String[] args) {
    BotCoreInitializer.init();

    var metrics = BotObservability.micrometer(9180);
    var tracer = BotObservability.otelTracer("bot");

    BotConfig config =
        BotConfig.builder()
            .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
            .build();

    BotAdapter adapter = BotAdapterImpl.builder().config(config).build();
    Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, "io.github.examples.simplebot");

    bot.start();
  }
}
