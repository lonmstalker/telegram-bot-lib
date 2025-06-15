package io.lonmstalker.examples.observability;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.bot.Bot;
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.BotFactory;
import io.lonmstalker.observability.ObservabilityInterceptor;
import io.lonmstalker.observability.impl.MicrometerCollector;
import io.lonmstalker.observability.impl.OTelTracer;

public class ObservabilityDemoApplication {
    public static void main(String[] args) {
        var metrics = MicrometerCollector.prometheus(9180);
        var tracer = OTelTracer.stdoutDev();

        BotConfig config = new BotConfig();
        config.addInterceptor(new ObservabilityInterceptor(metrics, tracer));

        BotAdapter adapter = update -> null;
        Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter,
                "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
