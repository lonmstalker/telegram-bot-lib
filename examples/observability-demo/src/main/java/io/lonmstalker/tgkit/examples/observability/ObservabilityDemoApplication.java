package io.lonmstalker.tgkit.examples.observability;

import io.lonmstalker.observability.BotObservability;
import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.observability.ObservabilityInterceptor;

public class ObservabilityDemoApplication {

    public static void main(String[] args) {
        var metrics = BotObservability.micrometer(9180);
        var tracer = BotObservability.otelTracer("bot");

        BotConfig config = BotConfig.builder()
                .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
                .build();

        BotAdapter adapter = update -> null;
        Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter,
                "io.lonmstalker.examples.simplebot");

        bot.start();
    }
}
