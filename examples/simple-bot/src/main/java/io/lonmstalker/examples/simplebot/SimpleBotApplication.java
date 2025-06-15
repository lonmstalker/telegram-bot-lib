package io.lonmstalker.examples.simplebot;

import io.lonmstalker.core.BotAdapter;
import io.lonmstalker.core.bot.Bot;
import io.lonmstalker.core.bot.BotConfig;
import io.lonmstalker.core.bot.BotFactory;

public class SimpleBotApplication {

    public static void main(String[] args) {
        BotConfig config = BotConfig.builder().build();
        BotAdapter adapter = update -> null; // actual logic handled via annotations

        Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
