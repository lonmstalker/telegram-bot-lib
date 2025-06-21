/**
 * Инструменты наблюдаемости: метрики и трассировки.
 *
 * <p>Содержит {@link io.github.tgkit.observability.ObservabilityInterceptor} и утилиты из {@link
 * io.github.tgkit.observability.BotObservability}.
 *
 * <p>Пример подключения:
 *
 * <pre>{@code
 * MetricsCollector metrics = BotObservability.micrometer(9180);
 * Tracer tracer = BotObservability.otelTracer("bot-service");
 * BotConfig cfg = BotConfig.builder()
 *         .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
 *         .build();
 * }</pre>
 */

package io.github.tgkit.observability;
