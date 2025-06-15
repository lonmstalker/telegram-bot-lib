package io.lonmstalker.tgkit.plugin.spi;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.Tracer;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.state.StateStore;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Контекст, через который плагин взаимодействует с ядром.
 */
public interface PluginContext {

    /**
     * Возвращает реестр активных ботов.
     */
    @NonNull
    BotRegistry bots();

    /**
     * Шина событий для взаимодействия между плагинами и ядром.
     */
    @NonNull
    EventBus bus();

    /** Хранилище состояния по умолчанию. */
    @NonNull
    StateStore defaultStore();

    /** HTTP-клиент. */
    @NonNull
    HttpClient httpClient();

    /** Открытие файла на чтение. */
    @NonNull
    InputStream readFile(@NonNull Path path) throws java.io.IOException;

    /** Открытие файла на запись. */
    @NonNull
    OutputStream writeFile(@NonNull Path path) throws java.io.IOException;

    /** Система метрик. */
    @NonNull
    MetricsCollector metrics();

    /** Система трассировки. */
    @NonNull
    Tracer tracer();

    /**
     * Конфигурационный раздел плагина. Может отсутствовать.
     *
     * @param pluginId идентификатор плагина
     * @return секция конфигурации или {@link Optional#empty()}
     */
    @NonNull
    Optional<ConfigSection> config(@NonNull String pluginId);

    /** Регистрация конфигурации плагина (используется менеджером). */
    default void registerConfig(@NonNull String pluginId, @NonNull ConfigSection config) {
        // по умолчанию ничего
    }
}
