# Security-bundle — шпаргалка 2.0

Это краткое руководство по использованию модуля безопасности TgKit.

## Быстрые юзкейсы

| Юзкейс | Что делаем | Аннотация / API |
|--------|------------|-----------------|
| Ограничить команду 5 запросами в минуту для каждого пользователя | `@RateLimit(key = USER, permits = 5, seconds = 60)` | `RateLimit` |
| Разрешить доступ только ADMIN-ам | `@Roles("ADMIN")` | `Roles` |
| Задать глобальный лимит 100 rps на бота | Аннотация `@RateLimit(key = GLOBAL, permits = 100, seconds = 1)` на классе или методе | `RateLimit` |
| Зашить лимиты и роли в конфиг без аннотаций | `security.yml` | YAML-конфиг |

## Новые возможности

* **Дефолтная inline-CAPTCHA** — модуль готов к работе без дополнительных зависимостей. Math-CAPTCHA активируется автоматически.
* **Несколько аннотаций `@RateLimit`** — можно комбинировать, например, лимиты `USER` и `GLOBAL` на одном методе. Аннотация стала `@Repeatable`.
* **Поддержка Spring Boot** — модуль `tgkit-security-starter` обеспечивает автоконфигурацию и аннотацию `@EnableTgKitSecurity`.

## Пример хэндлера с несколькими лимитами

```java
@BotHandler(type = MESSAGE)
@Roles("ADMIN")
@RateLimits({
    @RateLimit(key = USER,   permits = 5, seconds = 60),
    @RateLimit(key = GLOBAL, permits = 50, seconds = 10)
})
public void heavyCmd(BotRequest<Message> req) {
    // ...
}
```

При превышении первого указанного лимита пользователь увидит inline-CAPTCHA. После успешного решения счётчики обнуляются.

## Подключение в Spring Boot

```java
@Configuration
@EnableTgKitSecurity
public class BotConfig {
}
```

AutoConfiguration создаст `SecurityBundle` и добавит `SecurityInterceptor` в цепочку.
