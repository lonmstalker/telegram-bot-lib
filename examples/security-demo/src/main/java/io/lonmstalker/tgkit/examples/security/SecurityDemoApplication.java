package io.lonmstalker.tgkit.examples.security;

import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.security.BotSecurity;
import io.lonmstalker.tgkit.security.antispam.AntiSpamInterceptor;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;

import java.time.Duration;

public class SecurityDemoApplication {

    public static void main(String[] args) {

        RateLimiter rateLimiter = BotSecurity.inMemoryRateLimiter();
        CaptchaProvider captchaProvider =
                BotSecurity.inMemoryCaptchaProvider(Duration.ofMinutes(1), 100);
        AntiSpamInterceptor antiSpamInterceptor = AntiSpamInterceptor
                .builder()
                .flood(rateLimiter)
                .captcha(captchaProvider)
                .build();

        BotConfig cfg = BotConfig.builder()
                .globalInterceptor(antiSpamInterceptor)
                .build();

        BotAdapter adapter = BotAdapterImpl.builder()
                .config(cfg)
                .build();

        Bot bot = BotFactory.INSTANCE.from("TOKEN", cfg, adapter,
                "io.lonmstalker.examples.simplebot");
        bot.start();
    }
}
