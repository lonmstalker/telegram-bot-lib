/**
 * Набор средств для защиты Telegram-ботов.
 *
 * <p>Включает антиспам, CAPTCHA, лимиты и хранение секретов. Основные классы:
 *
 * <ul>
 *   <li>{@link io.lonmstalker.tgkit.security.BotSecurity} — фабрика типовых компонентов
 *   <li>{@link io.lonmstalker.tgkit.security.antispam.AntiSpamInterceptor} — фильтр спама
 *   <li>{@link io.lonmstalker.tgkit.security.ratelimit.RateLimitInterceptor} — ограничитель
 *       скорости
 * </ul>
 *
 * <p>Пример подключения:
 *
 * <pre>{@code
 * AntiSpamInterceptor guard = BotSecurity.antiSpamInterceptor(Set.of("spam.com"));
 * BotConfig cfg = BotConfig.builder()
 *         .globalInterceptor(guard)
 *         .build();
 * }</pre>
 */
package io.lonmstalker.tgkit.security;
