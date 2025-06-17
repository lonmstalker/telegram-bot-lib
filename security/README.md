# 📦 tgkit-security
*Модуль безопасности для Telegram-ботов на базе **tgkit***

---

## TL;DR ⚡
```java
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
);
```

## Возможности 🚀

| Категория        | Фича                                               | Что делает                                         |
|------------------|----------------------------------------------------|----------------------------------------------------|
| **DDoS / Abuse** | `RateLimitInterceptor`                             | Лимиты *per-user / chat / global* на вызовы команд |
| **Spam / Flood** | `AntiSpamInterceptor`                              | Дубликаты, частотка, репутация URL, DLP-фильтры    |
| **CAPTCHA**      | `MathCaptchaProvider`  <br>`SliderCaptchaProvider` | Текст-, touch- и web- (reCAPTCHA v3) варианты      |
| **Audit**        | `AuditBus` + конвертеры                            | Единый поток событий безопасности (JSON-ready)     |
| **Secrets**      | `SecretStore` SPI                                  | Ключи, токены, пароли — плаг-ин под Vault / AWS SM |
| **RBAC**         | `@RequiresRole`                                    | Проверка ролей                                     |

## Конфигурация 🔧
```java
SecurityGlobalConfig.INSTANCE
        .captcha(c -> c
            .type(CAPTCHA.SLIDER)
            .ttl(Duration.ofMinutes(10)))
        .rateLimits(r -> r
            .backend(InMemoryLimiter.create())
            .user(permits(30).per(Duration.ofMinutes(1))))
        .audit(a -> a.bus(KafkaAuditBus.create("sec-events")));

```