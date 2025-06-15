package io.lonmstalker.tgkit.core.bot;

/**
 * Документация по использованию интерфейса {@link Bot}.
 * <p>
 * Интерфейс предоставляет методы управления жизненным циклом бота
 * и доступа к его конфигурации.
 * </p>
 *
 * <p>Простейший пример создания бота:</p>
 *
 * <pre>{@code
 * BotConfig config = BotConfig.builder().build();
 * BotAdapter adapter = update -> null;
 * Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, "io.example.bot");
 * bot.start();
 * }</pre>
 */
public final class BotDocs {
    private BotDocs() {
    }
}
