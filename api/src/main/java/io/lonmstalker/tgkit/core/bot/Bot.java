package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Базовый интерфейс Telegram-бота.
 * <p>
 * Предоставляет методы управления жизненным циклом и доступ к метаданным бота.
 * </p>
 *
 * <p>
 * Пример создания и запуска простого бота:
 * </p>
 *
 * <pre>{@code
 * BotConfig config = BotConfig.builder().build();
 * BotAdapter adapter = update -> null;
 * Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, "io.example.bot");
 * bot.start();
 * }</pre>
 */
public interface Bot {

    /**
     * Возвращает внутренний идентификатор бота, используемый библиотекой.
     *
     * @return идентификатор бота
     * @throws BotApiException при попытке обратиться к значению до запуска бота
     */
    long internalId() throws BotApiException;

    /**
     * Возвращает идентификатор бота в Telegram.
     *
     * @return внешний идентификатор
     * @throws BotApiException если бот ещё не запущен или произошла ошибка API
     */
    long externalId() throws BotApiException;

    /**
     * Запускает бота и инициализирует соединение с Telegram.
     *
     * @throws BotApiException при ошибке инициализации или подключения
     */
    void start() throws BotApiException;

    /**
     * Останавливает работу бота и освобождает ресурсы.
     *
     * @throws BotApiException если возникли ошибки при завершении работы
     */
    void stop() throws BotApiException;

    /**
     * Имя пользователя бота в Telegram.
     *
     * @return username бота
     * @throws BotApiException если бот ещё не запущен
     */
    @NonNull
    String username() throws BotApiException;

    /**
     * Реестр команд, доступных для данного бота.
     *
     * @return объект реестра команд
     */
    @NonNull
    BotCommandRegistry registry();

    /**
     * Проверяет, запущен ли бот.
     *
     * @return {@code true}, если бот уже запущен
     */
    boolean isStarted();

    /**
     * Добавляет действие, выполняемое после остановки бота.
     *
     * @param action действие завершения
     */
    void onComplete(@NonNull BotCompleteAction action);

    /**
     * Текущая конфигурация бота.
     *
     * @return объект конфигурации
     */
    @NonNull
    BotConfig config();

    /**
     * Токен Telegram, использующийся при работе с API.
     *
     * @return строка токена
     */
    @NonNull
    String token();

    @NonNull
    BotRegistry botRegistry();
}
