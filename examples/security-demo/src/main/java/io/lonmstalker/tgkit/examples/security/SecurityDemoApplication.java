package io.lonmstalker.tgkit.examples.security;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.security.SecurityBundle;

public class SecurityDemoApplication {
    public static void main(String[] args) {
        SecurityBundle sec = SecurityBundle.builder().build();

        BotConfig cfg = BotConfig.builder()
                .addInterceptor(sec.interceptor())
                .build();

        BotAdapter adapter = update -> null;
        Bot bot = BotFactory.INSTANCE.from("TOKEN", cfg, adapter,
                "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
