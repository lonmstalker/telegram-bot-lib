package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;

public class SimpleBotApplication {

    public static void main(String[] args) {
        BotConfig config = BotConfig.builder().build();
        BotAdapter adapter = update -> null; // actual logic handled via annotations

        Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
