/**
 * Базовый набор классов для создания и запуска Telegram-бота.
 *
 * <p>Ключевые элементы:
 *
 * <ul>
 *   <li>{@link io.github.tgkit.internal.bot.BotFactory} — фабрика ботов
 *   <li>{@link io.github.tgkit.internal.bot.BotBuilder} — fluent API для быстрого запуска
 *   <li>{@link io.github.tgkit.internal.bot.BotAdapterImpl} — адаптер входящих событий
 * </ul>
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * Bot bot = BotFactory.INSTANCE.from(
 *         token,
 *         BotConfig.builder().build(),
 *         update -> null,
 *         "com.example.bot");
 * bot.start();
 * }</pre>
 */
package io.github.tgkit.internal.bot;
