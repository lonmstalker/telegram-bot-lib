/**
 * Инструменты наблюдаемости: метрики и трассировки.
 *
 * <p>Содержит {@link io.github.observability.ObservabilityInterceptor} и утилиты из {@link
 * io.github.observability.BotObservability}.
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

package io.github.observability;
