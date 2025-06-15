package io.lonmstalker.examples.observability;

import io.lonmstalker.core.bot.Bot;
import io.lonmstalker.core.bot.BotAdapter;
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.BotFactory;
import io.lonmstaler.observability.ObservabilityExceptionHandler;
import io.lonmstaler.observability.ObservabilityInterceptor;
import io.lonmstaler.observability.impl.MicrometerCollector;
import io.lonmstaler.observability.impl.OTelTracer;

public class ObservabilityDemoApplication {
    public static void main(String[] args) {
        var metrics = MicrometerCollector.prometheus(9180);
        var tracer = OTelTracer.stdoutDev();

        BotConfig config = new BotConfig();
        config.addInterceptor(new ObservabilityInterceptor(metrics, tracer));
        config.setGlobalExceptionHandler(new ObservabilityExceptionHandler(null, metrics));

        BotAdapter adapter = update -> null;
        Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter,
                "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
