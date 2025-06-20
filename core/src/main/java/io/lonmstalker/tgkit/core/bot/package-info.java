/**
 * Базовый набор классов для создания и запуска Telegram-бота.
 *
 * <p>Ключевые элементы:
 * <ul>
 *   <li>{@link io.lonmstalker.tgkit.core.bot.BotFactory} — фабрика ботов</li>
 *   <li>{@link io.lonmstalker.tgkit.core.bot.BotBuilder} — fluent API для быстрого запуска</li>
 *   <li>{@link io.lonmstalker.tgkit.core.bot.BotAdapterImpl} — адаптер входящих событий</li>
 * </ul>
 *
 * <p>Пример использования:
 * <pre>{@code
 * Bot bot = BotFactory.INSTANCE.from(
 *         token,
 *         BotConfig.builder().build(),
 *         update -> null,
 *         "com.example.bot");
 * bot.start();
 * }</pre>
 */
package io.lonmstalker.tgkit.core.bot;
